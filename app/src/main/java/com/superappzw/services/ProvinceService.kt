package com.superappzw.services

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProvinceService {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchProvinces(): List<String> {
        val snapshot = db
            .collection("provinces")
            .get()
            .await()

        val document = snapshot.documents.firstOrNull() ?: return emptyList()
        val data = document.data ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val provinces = data["region"] as? List<String> ?: return emptyList()

        return provinces.sorted()
    }
}