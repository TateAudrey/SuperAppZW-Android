package com.superappzw.services

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.superappzw.model.DailyLanguageError
import com.superappzw.model.DailyLanguageError.Unauthenticated
import com.superappzw.model.DailyLanguageModel
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class DailyLanguageService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val allLanguageIDs = listOf(
        "Chewa", "ChiShona", "Chibarwe", "English", "Kalanga",
        "Koisan", "Nambya", "Ndau", "Setswana", "Shangani",
        "SignLanguage", "Sotho", "Tshivenda", "Vakawanga",
        "isiNdebele", "isiXhosa"
    )

    // ── Public entry point ────────────────────────────────────────────────────

    suspend fun fetchTodaysLanguage(): DailyLanguageModel {
        val userID = currentUserID()

        val queueRef = db
            .collection("users")
            .document(userID)
            .collection("meta")
            .document("languageQueue")

        val snapshot = queueRef.get().await()
        val data = snapshot.data

        return if (data != null) {
            @Suppress("UNCHECKED_CAST")
            val queue = data["queue"] as? List<String>
            if (queue != null) {
                resolveLanguage(data = data, queue = queue, ref = queueRef)
            } else {
                createFreshQueue(ref = queueRef)
            }
        } else {
            // First time — no document exists yet
            createFreshQueue(ref = queueRef)
        }
    }

    // ── Queue resolution ──────────────────────────────────────────────────────

    private suspend fun resolveLanguage(
        data: Map<String, Any>,
        queue: List<String>,
        ref: DocumentReference,
    ): DailyLanguageModel {
        val lastServedTimestamp = data["lastServedDate"] as? Timestamp
        val lastServedDate = lastServedTimestamp?.toDate()
        val currentIndex = (data["currentIndex"] as? Long)?.toInt() ?: 0

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Same day — return the same language, no advancement
        if (lastServedDate != null) {
            val lastCal = Calendar.getInstance().apply { time = lastServedDate }
            val todayCal = Calendar.getInstance().apply { time = today }
            val isSameDay = lastCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                    lastCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

            if (isSameDay) {
                val languageID = queue[currentIndex]
                return fetchDocument(languageID)
            }
        }

        // New day — advance to next index
        val nextIndex = currentIndex + 1

        // Exhausted the queue — reshuffle and start over
        if (nextIndex >= queue.size) {
            return createFreshQueue(ref = ref)
        }

        // Advance queue pointer
        ref.update(
            mapOf(
                "currentIndex" to nextIndex,
                "lastServedDate" to Timestamp(today),
            )
        ).await()

        val languageID = queue[nextIndex]
        return fetchDocument(languageID)
    }

    // ── Fresh queue ───────────────────────────────────────────────────────────

    private suspend fun createFreshQueue(ref: DocumentReference): DailyLanguageModel {
        val shuffled = allLanguageIDs.shuffled()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        ref.set(
            mapOf(
                "queue" to shuffled,
                "currentIndex" to 0,
                "lastServedDate" to Timestamp(today),
            )
        ).await()

        return fetchDocument(shuffled[0])
    }

    // ── Firestore document fetch ───────────────────────────────────────────────

    private suspend fun fetchDocument(languageID: String): DailyLanguageModel {
        val snapshot = db
            .collection("languages")
            .document(languageID)
            .get()
            .await()

        val data = snapshot.data
            ?: throw DailyLanguageError.MissingFields(languageID)

        val greeting = data["greeting"] as? String
            ?: throw DailyLanguageError.MissingFields(languageID)

        val summary = data["summary"] as? String
            ?: throw DailyLanguageError.MissingFields(languageID)

        return DailyLanguageModel(
            languageID = languageID,
            greeting = greeting,
            summary = summary,
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentUserID(): String {
        return auth.currentUser?.uid
            ?: throw Unauthenticated
    }
}