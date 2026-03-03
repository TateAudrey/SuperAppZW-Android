package com.superappzw.ui.home.greeting

import android.content.Context
import androidx.core.content.edit
import java.util.Calendar

class RainbowGlowStorage(context: Context) {

    private val prefs = context.getSharedPreferences("rainbow_glow", Context.MODE_PRIVATE)
    private val key = "rainbowGlow_lastTappedDate"

    // ── Check if already tapped today ─────────────────────────────────────────

    val hasBeenTappedToday: Boolean
        get() {
            val lastTapped = prefs.getLong(key, -1L)
            if (lastTapped == -1L) return false
            return isSameDay(lastTapped, todayStartMillis())
        }

    // ── Record tap ────────────────────────────────────────────────────────────

    fun recordTap() {
        prefs.edit { putLong(key, todayStartMillis()) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun todayStartMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun isSameDay(a: Long, b: Long): Boolean {
        val calA = Calendar.getInstance().apply { timeInMillis = a }
        val calB = Calendar.getInstance().apply { timeInMillis = b }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
                calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
    }
}