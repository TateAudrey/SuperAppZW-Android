package com.superappzw.model

import androidx.annotation.DrawableRes
import java.util.UUID
import com.superappzw.R

data class OnboardingModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    @param:DrawableRes val cardImage: Int
)


val events = listOf(
    OnboardingModel(
        title = "Jobs & Gigs",
        subtitle = "Get paid for what you do best",
        cardImage = R.drawable.onboarding_image_jobs
    ),
    OnboardingModel(
        title = "Produce",
        subtitle = "Looking or selling produce?",
        cardImage = R.drawable.onboarding_image_produce
    ),
    OnboardingModel(
        title = "Home Services",
        subtitle = "Looking or offering household help?",
        cardImage = R.drawable.onboarding_image_services
    ),
    OnboardingModel(
        title = "Real Estate",
        subtitle = "Looking or selling a new home?",
        cardImage = R.drawable.onboarding_image_home
    )
)

