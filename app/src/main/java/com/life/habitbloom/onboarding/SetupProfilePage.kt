package com.life.habitbloom.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.components.ProgressDots
import com.life.habitbloom.components.SecondaryButton
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.ui.theme.*

@Composable
fun SetupProfilePage(
    userName: String,
    companionName: String,
    selectedGender: GenderType,
    selectedCompanion: CompanionType,
    wakeUpTime: String,
    sleepHours: Int,
    onUserNameChange: (String) -> Unit,
    onCompanionNameChange: (String) -> Unit,
    onGenderSelected: (GenderType) -> Unit,
    onWakeUpTimeChange: (String) -> Unit,
    onSleepHoursChange: (Int) -> Unit,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val canStartJourney =
        userName.trim().isNotEmpty() &&
                companionName.trim().isNotEmpty() &&
                wakeUpTime.trim().isNotEmpty() &&
                sleepHours in 1..12

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .padding(horizontal = 18.dp)
            .padding(top = 26.dp, bottom = 14.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressDots(currentPage = 3)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set up your profile",
            color = HabitTextDark,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us about yourself, your buddy,\nand your sleep routine.",
            color = HabitTextGrey,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, HabitBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HabitCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    SectionLabel("Your name :")

                    HabitTextField(
                        value = userName,
                        onValueChange = onUserNameChange,
                        placeholder = "Enter your name"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionLabel("Gender :")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GenderChip(
                            icon = "👧",
                            text = "Girl",
                            selected = selectedGender == GenderType.GIRL,
                            onClick = { onGenderSelected(GenderType.GIRL) },
                            modifier = Modifier.weight(1f)
                        )

                        GenderChip(
                            icon = "👦",
                            text = "Boy",
                            selected = selectedGender == GenderType.BOY,
                            onClick = { onGenderSelected(GenderType.BOY) },
                            modifier = Modifier.weight(1f)
                        )

                        GenderChip(
                            icon = "🙂",
                            text = "Prefer not\nto say",
                            selected = selectedGender == GenderType.PREFER_NOT_TO_SAY,
                            onClick = { onGenderSelected(GenderType.PREFER_NOT_TO_SAY) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionLabel("Buddy name :")

                    HabitTextField(
                        value = companionName,
                        onValueChange = onCompanionNameChange,
                        placeholder = "Enter buddy name"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionLabel("Wake up time :")

                    HabitTextField(
                        value = wakeUpTime,
                        onValueChange = onWakeUpTimeChange,
                        placeholder = "Example: 07:00"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionLabel("Sleep goal :")

                    SleepHoursSelector(
                        sleepHours = sleepHours,
                        onSleepHoursChange = onSleepHoursChange
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SectionLabel("Your chosen companion")

                    Spacer(modifier = Modifier.height(10.dp))

                    ChosenCompanionCard(selectedCompanion = selectedCompanion)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Start My journey",
            onClick = {
                if (canStartJourney) {
                    onStartClick()
                }
            },
            enabled = canStartJourney
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = HabitTextDark,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp
    )

    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun HabitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                color = HabitTextGrey
            )
        },
        textStyle = TextStyle(
            color = HabitTextDark,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = HabitCream,
            unfocusedContainerColor = HabitCream,
            focusedIndicatorColor = HabitGreen,
            unfocusedIndicatorColor = HabitBorder,
            cursorColor = HabitGreen
        ),
        singleLine = true
    )
}

@Composable
private fun SleepHoursSelector(
    sleepHours: Int,
    onSleepHoursChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(6, 7, 8, 9).forEach { hour ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(
                        width = 1.3.dp,
                        color = if (sleepHours == hour) HabitGreen else HabitBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(
                        color = if (sleepHours == hour) HabitLightGreen else HabitCard,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSleepHoursChange(hour) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${hour}h",
                    color = if (sleepHours == hour) HabitGreen else HabitTextDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun GenderChip(
    icon: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 1.3.dp,
                color = if (selected) HabitGreen else HabitBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = if (selected) HabitLightGreen else HabitCard,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 17.sp)

            Text(
                text = text,
                color = if (selected) HabitGreen else HabitTextDark,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = if (text.contains("\n")) 9.5.sp else 11.sp,
                lineHeight = 10.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ChosenCompanionCard(
    selectedCompanion: CompanionType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .border(1.2.dp, HabitBorder, RoundedCornerShape(17.dp)),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(132.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = selectedCompanion.imageRes),
                    contentDescription = selectedCompanion.title,
                    modifier = Modifier.requiredSize(170.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = selectedCompanion.title,
                    color = HabitTextDark,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = selectedCompanion.description,
                    color = HabitTextGrey,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}