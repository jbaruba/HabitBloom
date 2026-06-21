package com.life.habitbloom.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.BottomNavigationBar
import com.life.habitbloom.components.SettingDivider
import com.life.habitbloom.components.SettingRow
import com.life.habitbloom.components.SettingSectionTitle
import com.life.habitbloom.components.SettingToggleRow
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.model.UserProfile
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey

private enum class SettingsMode {
    OVERVIEW,
    EDIT_NAME,
    EDIT_GENDER,
    EDIT_COMPANION_NAME,
    CHANGE_COMPANION,
    EDIT_WAKE_UP_TIME,
    EDIT_SLEEP_HOURS
}

@Composable
fun SettingsPage(
    userProfile: UserProfile,
    currentLevel: Int,
    onUserNameChange: (String) -> Unit,
    onGenderChange: (GenderType) -> Unit,
    onCompanionNameChange: (String) -> Unit,
    onCompanionChange: (CompanionType) -> Unit,
    onWakeUpTimeChange: (String) -> Unit,
    onSleepHoursChange: (Int) -> Unit,
    onHomeClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onBuddyClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }

    var mode by remember { mutableStateOf(SettingsMode.OVERVIEW) }

    var tempUserName by remember(userProfile.userName) {
        mutableStateOf(userProfile.userName)
    }

    var tempGender by remember(userProfile.gender) {
        mutableStateOf(userProfile.gender)
    }

    var tempCompanionName by remember(userProfile.companionName) {
        mutableStateOf(userProfile.companionName)
    }

    var tempCompanionType by remember(userProfile.companionType) {
        mutableStateOf(userProfile.companionType)
    }

    var tempWakeUpTime by remember(userProfile.wakeUpTime) {
        mutableStateOf(userProfile.wakeUpTime)
    }

    var tempSleepHours by remember(userProfile.sleepHours) {
        mutableIntStateOf(userProfile.sleepHours)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            SettingsHeader()

            Spacer(modifier = Modifier.height(10.dp))

            when (mode) {
                SettingsMode.OVERVIEW -> SettingsOverviewContent(
                    userProfile = userProfile,
                    currentLevel = currentLevel,
                    dailyReminderEnabled = dailyReminderEnabled,
                    soundEffectsEnabled = soundEffectsEnabled,
                    onDailyReminderChange = { dailyReminderEnabled = it },
                    onSoundEffectsChange = { soundEffectsEnabled = it },
                    onEditNameClick = {
                        tempUserName = userProfile.userName
                        mode = SettingsMode.EDIT_NAME
                    },
                    onEditGenderClick = {
                        tempGender = userProfile.gender
                        mode = SettingsMode.EDIT_GENDER
                    },
                    onEditCompanionNameClick = {
                        tempCompanionName = userProfile.companionName
                        mode = SettingsMode.EDIT_COMPANION_NAME
                    },
                    onChangeCompanionClick = {
                        tempCompanionType = userProfile.companionType
                        mode = SettingsMode.CHANGE_COMPANION
                    },
                    onEditWakeUpTimeClick = {
                        tempWakeUpTime = userProfile.wakeUpTime
                        mode = SettingsMode.EDIT_WAKE_UP_TIME
                    },
                    onEditSleepHoursClick = {
                        tempSleepHours = userProfile.sleepHours
                        mode = SettingsMode.EDIT_SLEEP_HOURS
                    }
                )

                SettingsMode.EDIT_NAME -> EditTextSettingScreen(
                    title = "Change your name",
                    label = "Your name",
                    value = tempUserName,
                    onValueChange = { tempUserName = it },
                    originalValue = userProfile.userName,
                    onBackClick = {
                        tempUserName = userProfile.userName
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onUserNameChange(tempUserName.trim())
                        mode = SettingsMode.OVERVIEW
                    }
                )

                SettingsMode.EDIT_GENDER -> EditGenderScreen(
                    selectedGender = tempGender,
                    originalGender = userProfile.gender,
                    onGenderSelected = { tempGender = it },
                    onBackClick = {
                        tempGender = userProfile.gender
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onGenderChange(tempGender)
                        mode = SettingsMode.OVERVIEW
                    }
                )

                SettingsMode.EDIT_COMPANION_NAME -> EditTextSettingScreen(
                    title = "Rename companion",
                    label = "Buddy name",
                    value = tempCompanionName,
                    onValueChange = { tempCompanionName = it },
                    originalValue = userProfile.companionName,
                    onBackClick = {
                        tempCompanionName = userProfile.companionName
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onCompanionNameChange(tempCompanionName.trim())
                        mode = SettingsMode.OVERVIEW
                    }
                )

                SettingsMode.CHANGE_COMPANION -> ChangeCompanionScreen(
                    selectedCompanion = tempCompanionType,
                    originalCompanion = userProfile.companionType,
                    onCompanionSelected = { tempCompanionType = it },
                    onBackClick = {
                        tempCompanionType = userProfile.companionType
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onCompanionChange(tempCompanionType)
                        mode = SettingsMode.OVERVIEW
                    }
                )

                SettingsMode.EDIT_WAKE_UP_TIME -> EditTextSettingScreen(
                    title = "Change wake up time",
                    label = "Wake up time",
                    value = tempWakeUpTime,
                    onValueChange = { tempWakeUpTime = it },
                    originalValue = userProfile.wakeUpTime,
                    onBackClick = {
                        tempWakeUpTime = userProfile.wakeUpTime
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onWakeUpTimeChange(tempWakeUpTime.trim())
                        mode = SettingsMode.OVERVIEW
                    }
                )

                SettingsMode.EDIT_SLEEP_HOURS -> EditSleepHoursScreen(
                    selectedHours = tempSleepHours,
                    originalHours = userProfile.sleepHours,
                    onSleepHoursSelected = { tempSleepHours = it },
                    onBackClick = {
                        tempSleepHours = userProfile.sleepHours
                        mode = SettingsMode.OVERVIEW
                    },
                    onSaveClick = {
                        onSleepHoursChange(tempSleepHours)
                        mode = SettingsMode.OVERVIEW
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        BottomNavigationBar(
            selectedItem = "Settings",
            onHomeClick = onHomeClick,
            onProgressClick = onProgressClick,
            onBuddyClick = onBuddyClick,
            onRewardsClick = onRewardsClick,
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
private fun SettingsOverviewContent(
    userProfile: UserProfile,
    currentLevel: Int,
    dailyReminderEnabled: Boolean,
    soundEffectsEnabled: Boolean,
    onDailyReminderChange: (Boolean) -> Unit,
    onSoundEffectsChange: (Boolean) -> Unit,
    onEditNameClick: () -> Unit,
    onEditGenderClick: () -> Unit,
    onEditCompanionNameClick: () -> Unit,
    onChangeCompanionClick: () -> Unit,
    onEditWakeUpTimeClick: () -> Unit,
    onEditSleepHoursClick: () -> Unit
) {
    Text(
        text = "Settings",
        color = HabitTextDark,
        fontSize = 28.sp,
        lineHeight = 31.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Text(
        text = "Make the app fit your routine.",
        color = HabitTextGrey,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(12.dp))

    CurrentBuddyCard(
        companionType = userProfile.companionType,
        companionName = userProfile.companionName,
        currentLevel = currentLevel
    )

    Spacer(modifier = Modifier.height(10.dp))

    SettingSectionTitle(title = "You", icon = "👤")

    SettingsGroupCard {
        SettingRow(
            title = "Your name: ${userProfile.userName}",
            onClick = onEditNameClick
        )

        SettingDivider()

        SettingRow(
            title = "Gender: ${userProfile.gender.title}",
            onClick = onEditGenderClick
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    SettingSectionTitle(title = "Companion", icon = "🐾")

    SettingsGroupCard {
        SettingRow(
            title = "Rename companion: ${userProfile.companionName}",
            onClick = onEditCompanionNameClick
        )

        SettingDivider()

        SettingRow(
            title = "Change companion",
            onClick = onChangeCompanionClick
        )

        SettingDivider()

        CompanionChoicesRow(
            selectedCompanion = userProfile.companionType,
            onCompanionSelected = {}
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    SettingSectionTitle(title = "Sleep routine", icon = "🌙")

    SettingsGroupCard {
        SettingRow(
            title = "Wake up time: ${userProfile.wakeUpTime}",
            onClick = onEditWakeUpTimeClick
        )

        SettingDivider()

        SettingRow(
            title = "Sleep goal: ${userProfile.sleepHours} hours",
            onClick = onEditSleepHoursClick
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    SettingSectionTitle(title = "Notification", icon = "🔔")

    SettingsGroupCard {
        SettingToggleRow(
            title = "Daily reminder",
            checked = dailyReminderEnabled,
            onCheckedChange = onDailyReminderChange
        )

        SettingDivider()

        SettingRow(
            title = "Time to remind",
            onClick = {}
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    SettingSectionTitle(title = "App", icon = "⚙️")

    SettingsGroupCard {
        SettingToggleRow(
            title = "Sound effects",
            checked = soundEffectsEnabled,
            onCheckedChange = onSoundEffectsChange
        )
    }
}

@Composable
private fun EditTextSettingScreen(
    title: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    originalValue: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val canSave = value.trim().isNotEmpty() && value.trim() != originalValue.trim()

    Text(
        text = title,
        color = HabitTextDark,
        fontSize = 26.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingsGroupCard {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = label,
                color = HabitTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = HabitTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = HabitCream,
                    unfocusedContainerColor = HabitCream,
                    focusedIndicatorColor = HabitGreen,
                    unfocusedIndicatorColor = HabitBorder,
                    cursorColor = HabitGreen
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    SettingsActionButton(
        text = "Save change",
        enabled = canSave,
        onClick = onSaveClick
    )

    Spacer(modifier = Modifier.height(10.dp))

    SettingsBackButton(
        text = "Go back",
        onClick = onBackClick
    )
}

@Composable
private fun EditSleepHoursScreen(
    selectedHours: Int,
    originalHours: Int,
    onSleepHoursSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val canSave = selectedHours != originalHours

    Text(
        text = "Change sleep goal",
        color = HabitTextDark,
        fontSize = 26.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Text(
        text = "Choose how many hours you want to sleep.",
        color = HabitTextGrey,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingsGroupCard {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            listOf(6, 7, 8, 9).forEach { hour ->
                SleepHourChoiceCard(
                    hour = hour,
                    selected = selectedHours == hour,
                    onClick = { onSleepHoursSelected(hour) }
                )

                if (hour != 9) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    SettingsActionButton(
        text = "Save change",
        enabled = canSave,
        onClick = onSaveClick
    )

    Spacer(modifier = Modifier.height(10.dp))

    SettingsBackButton(
        text = "Go back",
        onClick = onBackClick
    )
}

@Composable
private fun SleepHourChoiceCard(
    hour: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) HabitGreen else HabitBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) HabitLightGreen else HabitCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "$hour hours sleep",
                color = if (selected) HabitGreen else HabitTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun EditGenderScreen(
    selectedGender: GenderType,
    originalGender: GenderType,
    onGenderSelected: (GenderType) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val canSave = selectedGender != originalGender

    Text(
        text = "Change gender",
        color = HabitTextDark,
        fontSize = 26.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(modifier = Modifier.height(16.dp))

    GenderChoiceCard(
        gender = GenderType.GIRL,
        selected = selectedGender == GenderType.GIRL,
        onClick = { onGenderSelected(GenderType.GIRL) }
    )

    Spacer(modifier = Modifier.height(10.dp))

    GenderChoiceCard(
        gender = GenderType.BOY,
        selected = selectedGender == GenderType.BOY,
        onClick = { onGenderSelected(GenderType.BOY) }
    )

    Spacer(modifier = Modifier.height(10.dp))

    GenderChoiceCard(
        gender = GenderType.PREFER_NOT_TO_SAY,
        selected = selectedGender == GenderType.PREFER_NOT_TO_SAY,
        onClick = { onGenderSelected(GenderType.PREFER_NOT_TO_SAY) }
    )

    Spacer(modifier = Modifier.height(18.dp))

    SettingsActionButton(
        text = "Save change",
        enabled = canSave,
        onClick = onSaveClick
    )

    Spacer(modifier = Modifier.height(10.dp))

    SettingsBackButton(
        text = "Go back",
        onClick = onBackClick
    )
}

@Composable
private fun ChangeCompanionScreen(
    selectedCompanion: CompanionType,
    originalCompanion: CompanionType,
    onCompanionSelected: (CompanionType) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val canSave = selectedCompanion != originalCompanion

    Text(
        text = "Change companion",
        color = HabitTextDark,
        fontSize = 26.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Text(
        text = "Choose a new buddy for your habit journey.",
        color = HabitTextGrey,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    CompanionChoicesRow(
        selectedCompanion = selectedCompanion,
        onCompanionSelected = onCompanionSelected
    )

    Spacer(modifier = Modifier.height(18.dp))

    SettingsActionButton(
        text = "Save change",
        enabled = canSave,
        onClick = onSaveClick
    )

    Spacer(modifier = Modifier.height(10.dp))

    SettingsBackButton(
        text = "Go back",
        onClick = onBackClick
    )
}

@Composable
private fun GenderChoiceCard(
    gender: GenderType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) HabitGreen else HabitBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) HabitLightGreen else HabitCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = gender.title,
                color = HabitTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(190.dp)
                .height(64.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_title),
                contentDescription = "Grow Daily",
                modifier = Modifier.requiredSize(190.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = CompanionType.PUPPY.imageForStage(2)),
                contentDescription = "Puppy",
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = CompanionType.SEED.imageForStage(2)),
                contentDescription = "Seed",
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = CompanionType.KITTEN.imageForStage(2)),
                contentDescription = "Kitten",
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun CurrentBuddyCard(
    companionType: CompanionType,
    companionName: String,
    currentLevel: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = companionType.imageForStage((currentLevel + 1).coerceIn(1, 6))
                ),
                contentDescription = companionName,
                modifier = Modifier.requiredSize(92.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(62.dp)
                    .background(HabitBorder)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Current Buddy",
                    color = HabitTextDark,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Name : $companionName",
                    color = HabitTextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Level : $currentLevel",
                    color = HabitTextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsGroupCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun CompanionChoicesRow(
    selectedCompanion: CompanionType,
    onCompanionSelected: (CompanionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompanionOptionMiniCard(
            companionType = CompanionType.SEED,
            selected = selectedCompanion == CompanionType.SEED,
            onClick = { onCompanionSelected(CompanionType.SEED) },
            modifier = Modifier.weight(1f)
        )

        CompanionOptionMiniCard(
            companionType = CompanionType.PUPPY,
            selected = selectedCompanion == CompanionType.PUPPY,
            onClick = { onCompanionSelected(CompanionType.PUPPY) },
            modifier = Modifier.weight(1f)
        )

        CompanionOptionMiniCard(
            companionType = CompanionType.KITTEN,
            selected = selectedCompanion == CompanionType.KITTEN,
            onClick = { onCompanionSelected(CompanionType.KITTEN) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CompanionOptionMiniCard(
    companionType: CompanionType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) HabitTextDark else HabitBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = companionType.imageRes),
                contentDescription = companionType.title,
                modifier = Modifier.requiredSize(90.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun SettingsActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(enabled = enabled) {
                if (enabled) onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) HabitGreen else HabitGreen.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = HabitCard,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SettingsBackButton(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = HabitTextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}