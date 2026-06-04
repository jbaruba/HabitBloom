package com.life.habitbloom.model

data class UserProfile(
    val userName: String = "",
    val gender: GenderType = GenderType.GIRL,
    val companionType: CompanionType = CompanionType.SEED,
    val companionName: String = "Sprout",
    val focusType: FocusType = FocusType.MOVEMENT
)