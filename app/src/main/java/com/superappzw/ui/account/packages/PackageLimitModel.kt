package com.superappzw.ui.account.packages

data class PackageLimitModel(
    val id: String,
    val name: String,
    val maxProducts: Int,
    val maxServices: Int,
) {
    companion object {
        fun fromData(id: String, data: Map<String, Any>): PackageLimitModel? {
            val name = data["name"] as? String
                ?: run {
                    println("Missing or wrong type for 'name': ${data["name"]}")
                    return null
                }

            val maxProducts = (data["max_products"] as? Long)?.toInt()
                ?: run {
                    println("Missing or wrong type for 'max_products': ${data["max_products"]}")
                    return null
                }

            val maxServices = (data["max_services"] as? Long)?.toInt()
                ?: run {
                    println("Missing or wrong type for 'max_services': ${data["max_services"]}")
                    return null
                }

            return PackageLimitModel(
                id = id,
                name = name,
                maxProducts = maxProducts,
                maxServices = maxServices,
            )
        }
    }
}