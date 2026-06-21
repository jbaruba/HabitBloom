package com.life.habitbloom.model

data class UserProfile(
    val onboardingCompleted: Boolean = false,
    val userName: String = "",
    val gender: GenderType = GenderType.GIRL,
    val companionType: CompanionType = CompanionType.SEED,
    val companionName: String = "",
    val focusTypes: List<FocusType> = listOf(FocusType.MOVEMENT),
    val wakeUpTime: String = "07:00",
    val sleepHours: Int = 8
)