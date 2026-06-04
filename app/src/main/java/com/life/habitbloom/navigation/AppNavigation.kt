package com.life.habitbloom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.life.habitbloom.home.HomePage
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.model.UserProfile
import com.life.habitbloom.onboarding.ChooseCompanionPage
import com.life.habitbloom.onboarding.ChooseFocusPage
import com.life.habitbloom.onboarding.IntroductionPage
import com.life.habitbloom.onboarding.SetupProfilePage
import com.life.habitbloom.settings.SettingsPage

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableIntStateOf(0) }

    var selectedCompanion by remember { mutableStateOf(CompanionType.SEED) }
    var selectedFocus by remember { mutableStateOf(FocusType.MOVEMENT) }
    var selectedGender by remember { mutableStateOf(GenderType.GIRL) }

    var userName by remember { mutableStateOf("") }
    var companionName by remember { mutableStateOf("") }

    val safeUserName = userName.ifBlank { "Luna" }
    val safeCompanionName = companionName.ifBlank { "Sprout" }

    val userProfile = UserProfile(
        userName = safeUserName,
        gender = selectedGender,
        companionType = selectedCompanion,
        companionName = safeCompanionName,
        focusType = selectedFocus
    )

    when (currentScreen) {
        0 -> IntroductionPage(
            onGetStartedClick = { currentScreen = 1 },
            onSkipClick = { currentScreen = 4 }
        )

        1 -> ChooseCompanionPage(
            selectedCompanion = selectedCompanion,
            onCompanionSelected = { selectedCompanion = it },
            onContinueClick = { currentScreen = 2 },
            onBackClick = { currentScreen = 0 }
        )

        2 -> ChooseFocusPage(
            selectedFocus = selectedFocus,
            onFocusSelected = { selectedFocus = it },
            onContinueClick = { currentScreen = 3 },
            onBackClick = { currentScreen = 1 }
        )

        3 -> SetupProfilePage(
            userName = userName,
            companionName = companionName,
            selectedGender = selectedGender,
            selectedCompanion = selectedCompanion,
            onUserNameChange = { userName = it },
            onCompanionNameChange = { companionName = it },
            onGenderSelected = { selectedGender = it },
            onStartClick = { currentScreen = 4 },
            onBackClick = { currentScreen = 2 }
        )

        4 -> HomePage(
            userName = safeUserName,
            companionName = safeCompanionName,
            companionType = selectedCompanion,
            onSettingsClick = {
                currentScreen = 5
            }
        )

        5 -> SettingsPage(
            userProfile = userProfile,
            currentLevel = 0,
            onUserNameChange = { userName = it },
            onGenderChange = { selectedGender = it },
            onCompanionNameChange = { companionName = it },
            onCompanionChange = { selectedCompanion = it },
            onHomeClick = {
                currentScreen = 4
            },
            onSettingsClick = {
                currentScreen = 5
            }
        )
    }
}