package com.superappzw.model

data class PackageLimitModel(
    val id: String,
    val name: String,       // ← add this
    val maxProducts: Int,   // ← was productLimit
    val maxServices: Int,   // ← was serviceLimit
) {
    companion object {
        fun fromData(id: String, data: Map<String, Any>): PackageLimitModel? {
            val name        = data["name"] as? String ?: return null
            val maxProducts = (data["max_products"] as? Long)?.toInt() ?: return null
            val maxServices = (data["max_services"] as? Long)?.toInt() ?: return null
            return PackageLimitModel(
                id = id,
                name = name,
                maxProducts = maxProducts,
                maxServices = maxServices,
            )
        }
    }
}