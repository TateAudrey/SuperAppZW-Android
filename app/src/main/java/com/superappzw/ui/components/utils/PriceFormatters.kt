package com.superappzw.ui.components.utils

fun Int.abbreviated(): String {
    return when {
        this >= 1_000_000 -> {
            val value = this / 1_000_000.0
            formatAbbreviated(value, suffix = "m")
        }
        this >= 1_000 -> {
            val value = this / 1_000.0
            formatAbbreviated(value, suffix = "k")
        }
        else -> "$this"
    }
}

private fun formatAbbreviated(value: Double, suffix: String): String {
    return if (value % 1.0 == 0.0) {
        "${value.toInt()}$suffix"
    } else {
        String.format("%.1f$suffix", value)
    }
}

// ── Double price formatting ───────────────────────────────────────────────────

fun Double.formattedPrice(currency: String): String {
    return when {
        this >= 1_000_000 -> {
            val value = this / 1_000_000.0
            "$currency ${formatAbbreviated(value, suffix = "m")}"
        }
        this >= 1_000 -> {
            val value = this / 1_000.0
            "$currency ${formatAbbreviated(value, suffix = "k")}"
        }
        else -> String.format("$currency %.2f", this)
    }
}