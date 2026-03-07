package com.superappzw.ui.lisitngs

enum class ListingTab(val label: String) {
    PRODUCTS("Products"),
    SERVICES("Services"),
    REVIEWS("Reviews"),
    POST("Post"),
}

enum class ListingType(val label: String) {
    PRODUCT("Product"),
    SERVICE("Service"),
}

enum class ListingCurrency(val label: String) {
    USD("USD"),
    ZWL("ZWL"),
    ZAR("ZAR"),
}