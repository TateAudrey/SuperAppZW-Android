package com.superappzw.ui.components.utils

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.superappzw.model.events
import com.superappzw.ui.components.cards.OnboardingCard
import kotlinx.coroutines.delay

@Composable
fun OnboardingCarousel(
    cardWidth: Int,
    spacing: Int
) {
    val listState = rememberLazyListState()
    val scrollSpeed = 0.3.dp // how many dp per frame
    val density = LocalDensity.current
    val scrollPx = with(density) { scrollSpeed.toPx() }

    // Repeat items to allow seamless scroll
    val repeatedEvents = remember { (1..3).flatMap { events } }

    LaunchedEffect(Unit) {
        while (true) {
            listState.scrollBy(scrollPx)
            delay(16) // ~60fps
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp),
        userScrollEnabled = false
    ) {
        items(repeatedEvents) { event ->
            OnboardingCard(
                model = event,
                cardWidth = cardWidth
            )
        }
    }
}




