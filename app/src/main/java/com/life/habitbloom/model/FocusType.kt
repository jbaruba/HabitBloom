package com.life.habitbloom.model

import androidx.annotation.DrawableRes
import com.life.habitbloom.R

enum class FocusType(
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int
) {
    MOVEMENT(
        title = "Movement",
        description = "move more and reduce inactivity",
        iconRes = R.drawable.page3_icon1
    ),
    HYDRATION(
        title = "Hydration",
        description = "drink water more consistently",
        iconRes = R.drawable.page3_icon2
    ),
    SLEEP(
        title = "Sleep",
        description = "build a healthier bedtime routine",
        iconRes = R.drawable.page3_icon3
    ),
    RELAXATION(
        title = "Relaxation",
        description = "take calm moments and reduce stress",
        iconRes = R.drawable.page3_icon4
    )
}