package com.superappzw.ui.home.billboard

data class BillboardItemModel(
    val id: String,
    val userID: String,
    val title: String,
    val detail: String,
    val imageURL: String?,
) {
    companion object {
        fun fromData(documentID: String, data: Map<String, Any>): BillboardItemModel? {
            val title = data["title"] as? String ?: return null
            val detail = data["description"] as? String ?: return null

            return BillboardItemModel(
                id = documentID,
                userID = documentID,
                title = title,
                detail = detail,
                imageURL = data["image_url"] as? String,
            )
        }
    }
}