package com.life.habitbloom.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.components.SecondaryButton
import com.life.habitbloom.data.UserPreferences
import com.life.habitbloom.exercise.HydrationChallengePage
import com.life.habitbloom.exercise.MovementChallengePage
import com.life.habitbloom.exercise.SleepChallengePage
import com.life.habitbloom.home.HomePage
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.model.UserProfile
import com.life.habitbloom.onboarding.ChooseCompanionPage
import com.life.habitbloom.onboarding.ChooseFocusPage
import com.life.habitbloom.onboarding.IntroductionPage
import com.life.habitbloom.onboarding.SetupProfilePage
import com.life.habitbloom.progress.ProgressPage
import com.life.habitbloom.settings.SettingsPage
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val savedProfile = remember { userPreferences.loadProfile() }

    var currentScreen by remember {
        mutableIntStateOf(
            if (savedProfile.onboardingCompleted) {
                HOME_SCREEN
            } else {
                INTRODUCTION_SCREEN
            }
        )
    }

    var selectedCompanion by remember { mutableStateOf(savedProfile.companionType) }
    var selectedGender by remember { mutableStateOf(savedProfile.gender) }

    val selectedFocuses = remember {
        mutableStateListOf<FocusType>().apply {
            addAll(savedProfile.focusTypes.take(3))
        }
    }

    var userName by remember { mutableStateOf(savedProfile.userName) }
    var companionName by remember { mutableStateOf(savedProfile.companionName) }
    var wakeUpTime by remember { mutableStateOf(savedProfile.wakeUpTime) }
    var sleepHours by remember { mutableStateOf(savedProfile.sleepHours) }

    var completedChallengeFocus by remember { mutableStateOf<FocusType?>(null) }

    val safeUserName = userName.ifBlank { "Luna" }
    val safeCompanionName = companionName.ifBlank { "Sprout" }

    fun currentProfile(onboardingCompleted: Boolean = true): UserProfile {
        return UserProfile(
            onboardingCompleted = onboardingCompleted,
            userName = safeUserName,
            gender = selectedGender,
            companionType = selectedCompanion,
            companionName = safeCompanionName,
            focusTypes = selectedFocuses.toList(),
            wakeUpTime = wakeUpTime,
            sleepHours = sleepHours
        )
    }

    fun saveCurrentProfile() {
        userPreferences.saveProfile(currentProfile(onboardingCompleted = true))
    }

    fun openExercisePage(focusType: FocusType) {
        currentScreen = when (focusType) {
            FocusType.MOVEMENT -> MOVEMENT_EXERCISE_SCREEN
            FocusType.HYDRATION -> HYDRATION_EXERCISE_SCREEN
            FocusType.SLEEP -> SLEEP_EXERCISE_SCREEN
            FocusType.RELAXATION -> RELAXATION_EXERCISE_SCREEN
        }
    }

    fun completeExercise(focusType: FocusType) {
        completedChallengeFocus = focusType
        currentScreen = HOME_SCREEN
    }

    when (currentScreen) {
        INTRODUCTION_SCREEN -> IntroductionPage(
            onGetStartedClick = {
                currentScreen = CHOOSE_COMPANION_SCREEN
            },
            onSkipClick = {
                currentScreen = CHOOSE_COMPANION_SCREEN
            }
        )

        CHOOSE_COMPANION_SCREEN -> ChooseCompanionPage(
            selectedCompanion = selectedCompanion,
            onCompanionSelected = {
                selectedCompanion = it
            },
            onContinueClick = {
                currentScreen = CHOOSE_FOCUS_SCREEN
            },
            onBackClick = {
                currentScreen = INTRODUCTION_SCREEN
            }
        )

        CHOOSE_FOCUS_SCREEN -> ChooseFocusPage(
            selectedFocuses = selectedFocuses,
            onFocusToggle = { focus ->
                if (selectedFocuses.contains(focus)) {
                    selectedFocuses.remove(focus)
                } else {
                    if (selectedFocuses.size < 3) {
                        selectedFocuses.add(focus)
                    }
                }
            },
            onContinueClick = {
                if (selectedFocuses.size == 3) {
                    currentScreen = SETUP_PROFILE_SCREEN
                }
            },
            onBackClick = {
                currentScreen = CHOOSE_COMPANION_SCREEN
            }
        )

        SETUP_PROFILE_SCREEN -> SetupProfilePage(
            userName = userName,
            companionName = companionName,
            selectedGender = selectedGender,
            selectedCompanion = selectedCompanion,
            wakeUpTime = wakeUpTime,
            sleepHours = sleepHours,
            onUserNameChange = {
                userName = it
            },
            onCompanionNameChange = {
                companionName = it
            },
            onGenderSelected = {
                selectedGender = it
            },
            onWakeUpTimeChange = {
                wakeUpTime = it
            },
            onSleepHoursChange = {
                sleepHours = it
            },
            onStartClick = {
                saveCurrentProfile()
                currentScreen = HOME_SCREEN
            },
            onBackClick = {
                currentScreen = CHOOSE_FOCUS_SCREEN
            }
        )

        HOME_SCREEN -> HomePage(
            userName = safeUserName,
            companionName = safeCompanionName,
            companionType = selectedCompanion,
            focusTypes = selectedFocuses.toList(),
            completedChallengeFocus = completedChallengeFocus,
            onCompletedChallengeHandled = {
                completedChallengeFocus = null
            },
            onChallengeClick = { focusType ->
                openExercisePage(focusType)
            },
            onSettingsClick = {
                currentScreen = SETTINGS_SCREEN
            }
        )

        SETTINGS_SCREEN -> SettingsPage(
            userProfile = currentProfile(onboardingCompleted = true),
            currentLevel = calculateCurrentLevel(userPreferences.getTotalXp()),
            onUserNameChange = {
                userName = it
                saveCurrentProfile()
            },
            onGenderChange = {
                selectedGender = it
                saveCurrentProfile()
            },
            onCompanionNameChange = {
                companionName = it
                saveCurrentProfile()
            },
            onCompanionChange = {
                selectedCompanion = it
                saveCurrentProfile()
            },
            onWakeUpTimeChange = {
                wakeUpTime = it
                saveCurrentProfile()
            },
            onSleepHoursChange = {
                sleepHours = it
                saveCurrentProfile()
            },
            onHomeClick = {
                saveCurrentProfile()
                currentScreen = HOME_SCREEN
            },
            onProgressClick = {
                saveCurrentProfile()
                currentScreen = PROGRESS_SCREEN
            },
            onBuddyClick = {},
            onRewardsClick = {},
            onSettingsClick = {
                currentScreen = SETTINGS_SCREEN
            }
        )

        PROGRESS_SCREEN -> ProgressPage(
            totalXp = userPreferences.getTotalXp(),
            streak = userPreferences.getStreak(),
            focusTypes = selectedFocuses.toList(),
            onHomeClick = {
                currentScreen = HOME_SCREEN
            },
            onSettingsClick = {
                currentScreen = SETTINGS_SCREEN
            }
        )

        HYDRATION_EXERCISE_SCREEN -> HydrationChallengePage(
            companionName = safeCompanionName,
            onBackClick = {
                currentScreen = HOME_SCREEN
            },
            onChallengeCompleted = {
                completeExercise(FocusType.HYDRATION)
            }
        )

        MOVEMENT_EXERCISE_SCREEN -> MovementChallengePage(
            companionName = safeCompanionName,
            userGender = selectedGender,
            onBackClick = {
                currentScreen = HOME_SCREEN
            },
            onChallengeCompleted = {
                completeExercise(FocusType.MOVEMENT)
            }
        )

        SLEEP_EXERCISE_SCREEN -> SleepChallengePage(
            companionName = safeCompanionName,
            bedtimeGoal = "11:00 PM",
            sleepGoalHours = sleepHours,
            onBackClick = {
                currentScreen = HOME_SCREEN
            },
            onChallengeCompleted = {
                completeExercise(FocusType.SLEEP)
            }
        )

        RELAXATION_EXERCISE_SCREEN -> ExercisePlaceholderPage(
            title = "Relaxation Challenge",
            description = "This page will guide the user through a short breathing exercise.",
            onBackClick = {
                currentScreen = HOME_SCREEN
            },
            onCompleteClick = {
                completeExercise(FocusType.RELAXATION)
            }
        )
    }
}

@Composable
private fun ExercisePlaceholderPage(
    title: String,
    description: String,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = HabitTextDark,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            color = HabitTextGrey,
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            text = "Complete for now",
            onClick = onCompleteClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}

private fun calculateCurrentLevel(totalXp: Int): Int {
    return when {
        totalXp < 50 -> 0
        totalXp < 150 -> 1
        totalXp < 300 -> 2
        totalXp < 500 -> 3
        totalXp < 800 -> 4
        totalXp < 1100 -> 5
        else -> 6
    }
}

private const val INTRODUCTION_SCREEN = 0
private const val CHOOSE_COMPANION_SCREEN = 1
private const val CHOOSE_FOCUS_SCREEN = 2
private const val SETUP_PROFILE_SCREEN = 3
private const val HOME_SCREEN = 4
private const val SETTINGS_SCREEN = 5
private const val PROGRESS_SCREEN = 6
private const val HYDRATION_EXERCISE_SCREEN = 7
private const val MOVEMENT_EXERCISE_SCREEN = 8
private const val SLEEP_EXERCISE_SCREEN = 9
private const val RELAXATION_EXERCISE_SCREEN = 10