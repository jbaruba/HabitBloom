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
        val safeStage = stage.coerceIn(1, 6)

        return when (this) {
            SEED -> when (safeStage) {
                1 -> R.drawable.plant_1
                2 -> R.drawable.plant_2
                3 -> R.drawable.plant_3
                4 -> R.drawable.plant_4
                5 -> R.drawable.plant_5
                else -> R.drawable.plant_6
            }

            PUPPY -> when (safeStage) {
                1 -> R.drawable.dog_1
                2 -> R.drawable.dog_2
                3 -> R.drawable.dog_3
                4 -> R.drawable.dog_4
                5 -> R.drawable.dog_5
                else -> R.drawable.dog_6
            }

            KITTEN -> when (safeStage) {
                1 -> R.drawable.cat_1
                2 -> R.drawable.cat_2
                3 -> R.drawable.cat_3
                4 -> R.drawable.cat_4
                5 -> R.drawable.cat_5
                else -> R.drawable.cat_6
            }
        }
    }
}