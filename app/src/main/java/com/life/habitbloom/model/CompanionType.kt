package com.life.habitbloom.model

import androidx.annotation.DrawableRes
import com.life.habitbloom.R

enum class CompanionType(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
) {
    SEED(
        title = "Seed",
        description = "calm and steady growth",
        imageRes = R.drawable.plant_1
    ),
    PUPPY(
        title = "Puppy",
        description = "playful and energetic",
        imageRes = R.drawable.dog_1
    ),
    KITTEN(
        title = "Kitten",
        description = "curious and cheerful",
        imageRes = R.drawable.cat_1
    );

    @DrawableRes
    fun imageForStage(stage: Int): Int {
        val safeStage = stage.coerceIn(1, 3)

        return when (this) {
            SEED -> when (safeStage) {
                1 -> R.drawable.plant_1
                2 -> R.drawable.plant_2
                else -> R.drawable.plant_3
            }

            PUPPY -> when (safeStage) {
                1 -> R.drawable.dog_1
                2 -> R.drawable.dog_3
                else -> R.drawable.dog_3
            }

            KITTEN -> when (safeStage) {
                1 -> R.drawable.cat_1
                2 -> R.drawable.cat_3
                else -> R.drawable.cat_3
            }
        }
    }
}