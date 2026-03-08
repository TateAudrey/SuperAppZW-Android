package com.superappzw.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.superappzw.ui.home.billboard.BillboardItemModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class BillboardService {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun fetchBillboards(): List<BillboardItemModel> = coroutineScope {
        val snapshot = db.collection("billboard").get().await()

        // Mirror Swift's withThrowingTaskGroup — resolve all in parallel
        snapshot.documents
            .map { doc ->
                async {
                    val data = doc.data?.toMutableMap() ?: return@async null

                    // Resolve gs:// → https:// before building the model
                    val gsURL = data["image_url"] as? String
                    if (gsURL != null) {
                        data["image_url"] = resolveDownloadURL(gsURL) ?: gsURL
                    }

                    BillboardItemModel.fromData(documentID = doc.id, data = data)
                }
            }
            .awaitAll()
            .filterNotNull()
    }

    // ── Resolve gs:// storage URL to a public https:// download URL ───────────

    private suspend fun resolveDownloadURL(gsURL: String): String? {
        return try {
            val ref = storage.getReferenceFromUrl(gsURL)
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}