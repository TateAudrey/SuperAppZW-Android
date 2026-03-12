package com.superappzw.services

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.superappzw.ui.store.StoreListing
import kotlinx.coroutines.tasks.await

class FavouritesService private constructor() {

    companion object {
        val shared = FavouritesService()
    }

    private val db = FirebaseFirestore.getInstance()

    private fun itemsRef(uid: String) = db
        .collection("favourites")
        .document(uid)
        .collection("items")

    // ── Toggle (add or remove) ────────────────────────────────────────────────

    suspend fun toggle(listing: StoreListing): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val ref = itemsRef(uid).document(listing.itemCode)
        val doc = ref.get().await()

        return if (doc.exists()) {
            ref.delete().await()
            false
        } else {
            val data = mapOf(
                "itemCode"     to listing.itemCode,
                "title"        to listing.title,
                "description"  to listing.description,
                "price"        to listing.price,
                "currency"     to listing.currency,
                "isNegotiable" to listing.isNegotiable,
                "imageURL"     to (listing.imageURL?.toString() ?: ""), // ← always writes a String
                "viewCount"    to listing.viewCount,
                "ownerUserID"  to listing.ownerUserID,
                "location"     to listing.location,
                "savedAt"      to FieldValue.serverTimestamp(),
            )
            ref.set(data).await()
            true
        }
    }

    // ── Check if favourited ───────────────────────────────────────────────────

    suspend fun isFavourited(itemCode: String): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return runCatching {
            itemsRef(uid).document(itemCode).get().await().exists()
        }.getOrDefault(false)
    }

    // ── Fetch all favourites ──────────────────────────────────────────────────

    suspend fun fetchAll(): List<StoreListing> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()

        val snapshot = itemsRef(uid)
            .get()  // ← removed orderBy("savedAt")
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val d = doc.data ?: return@mapNotNull null

            val itemCode    = d["itemCode"]    as? String ?: return@mapNotNull null
            val title       = d["title"]       as? String ?: return@mapNotNull null
            val currency    = d["currency"]    as? String ?: return@mapNotNull null
            val ownerUserID = d["ownerUserID"] as? String ?: return@mapNotNull null
            val isNegotiable = d["isNegotiable"] as? Boolean ?: false

            val price = if (isNegotiable) 0.0 else when (val raw = d["price"]) {
                is Double -> raw
                is Long   -> raw.toDouble()
                is String -> raw.toDoubleOrNull() ?: 0.0
                else      -> 0.0
            }

            val viewCount = when (val raw = d["viewCount"]) {
                is Long -> raw.toInt()
                is Int  -> raw
                else    -> 0
            }

            val imageURL = when (val raw = d["imageURL"]) {
                is String -> raw.takeIf { it.isNotBlank() }?.let { android.net.Uri.parse(it) }
                else      -> raw?.toString()?.takeIf { it.isNotBlank() }?.let { android.net.Uri.parse(it) }
            }

            StoreListing(
                title        = title,
                description  = d["description"] as? String ?: "",
                price        = price,
                currency     = currency,
                itemCode     = itemCode,
                imageURL     = imageURL?.toString(),
                viewCount    = viewCount,
                ownerUserID  = ownerUserID,
                location     = d["location"] as? String ?: "",
                isNegotiable = isNegotiable,
            )
        }
    }

    // ── Remove ────────────────────────────────────────────────────────────────

    suspend fun remove(itemCode: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        itemsRef(uid).document(itemCode).delete().await()
    }
}