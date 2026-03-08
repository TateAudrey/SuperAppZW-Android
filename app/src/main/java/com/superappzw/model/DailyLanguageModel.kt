package com.superappzw.model

data class DailyLanguageModel(
    val languageID: String,
    val greeting: String,
    val summary: String,
)

sealed class DailyLanguageError(message: String) : Exception(message) {
    class MissingFields(languageID: String) :
        DailyLanguageError("Document '$languageID' is missing greeting or summary fields.")
    
    data object Unauthenticated : DailyLanguageError("No authenticated user found.")
}