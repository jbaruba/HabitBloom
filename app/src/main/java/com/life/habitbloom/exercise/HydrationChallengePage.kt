package com.life.habitbloom.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.data.UserPreferences
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import kotlinx.coroutines.delay

@Composable
fun HydrationChallengePage(
    companionName: String,
    onBackClick: () -> Unit,
    onChallengeCompleted: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val today = userPreferences.today()
    val savedHydrationDate = userPreferences.getHydrationDate()
    val isSameDay = savedHydrationDate == today

    val waterCount = remember {
        mutableIntStateOf(
            if (isSameDay) {
                userPreferences.getHydrationWaterCount().coerceIn(0, WATER_GOAL)
            } else {
                0
            }
        )
    }

    val lastDrinkTimeMillis = remember {
        mutableLongStateOf(
            if (isSameDay) {
                userPreferences.getHydrationLastDrinkTimeMillis()
            } else {
                0L
            }
        )
    }

    var cooldownSecondsLeft by remember {
        mutableIntStateOf(calculateCooldownSeconds(lastDrinkTimeMillis.longValue))
    }

    var showCompletionPopup by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (!isSameDay) {
            userPreferences.saveHydrationState(
                waterCount = 0,
                lastDrinkTimeMillis = 0L,
                hydrationDate = today
            )
        }
    }

    LaunchedEffect(lastDrinkTimeMillis.longValue, waterCount.intValue) {
        while (waterCount.intValue < WATER_GOAL) {
            cooldownSecondsLeft = calculateCooldownSeconds(lastDrinkTimeMillis.longValue)
            delay(1000)
        }

        cooldownSecondsLeft = 0
    }

    fun saveHydrationProgress() {
        userPreferences.saveHydrationState(
            waterCount = waterCount.intValue,
            lastDrinkTimeMillis = lastDrinkTimeMillis.longValue,
            hydrationDate = today
        )
    }

    fun drinkWater() {
        if (waterCount.intValue >= WATER_GOAL) {
            showCompletionPopup = true
            return
        }

        if (cooldownSecondsLeft > 0) {
            return
        }

        val newWaterCount = (waterCount.intValue + 1).coerceAtMost(WATER_GOAL)

        waterCount.intValue = newWaterCount
        lastDrinkTimeMillis.longValue = System.currentTimeMillis()
        cooldownSecondsLeft = calculateCooldownSeconds(lastDrinkTimeMillis.longValue)

        saveHydrationProgress()

        if (newWaterCount >= WATER_GOAL) {
            showCompletionPopup = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HydrationTopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hydration Challenge",
                color = HabitTextDark,
                fontSize = 27.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Drink water and water your buddy!",
                color = HabitTextDark,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            HydrationHero()

            Spacer(modifier = Modifier.height(9.dp))

            HydrationTipCard()

            Spacer(modifier = Modifier.height(9.dp))

            HydrationGoalCard(
                waterCount = waterCount.intValue
            )

            Spacer(modifier = Modifier.height(10.dp))

            HydrationActionButton(
                waterCount = waterCount.intValue,
                cooldownSecondsLeft = cooldownSecondsLeft,
                onClick = {
                    if (waterCount.intValue >= WATER_GOAL) {
                        showCompletionPopup = true
                    } else {
                        drinkWater()
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (showCompletionPopup) {
            HydrationCompletionPopup(
                companionName = companionName,
                onFinishClick = onChallengeCompleted
            )
        }
    }
}

@Composable
private fun HydrationTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clip(CircleShape)
                .background(HabitCard)
                .border(
                    width = 1.dp,
                    color = HabitBorder,
                    shape = CircleShape
                )
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = HabitTextDark,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            painter = painterResource(id = R.drawable.home_title),
            contentDescription = "Grow Daily",
            modifier = Modifier
                .align(Alignment.Center)
                .requiredSize(122.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun HydrationHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(218.dp)
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.hydration_background),
            contentDescription = "Hydration background",
            modifier = Modifier
                .fillMaxWidth()
                .height(315.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (0).dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.hydration_plant),
            contentDescription = "Hydration plant",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .requiredSize(198.dp)
                .offset(y = 7.dp),
            contentScale = ContentScale.Fit
        )

        Card(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 38.dp)
                .offset(y = (-28).dp),
            shape = RoundedCornerShape(21.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "I drink\nwhen you\ndrink! ❤️",
                color = HabitTextDark,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun HydrationTipCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = RoundedCornerShape(17.dp)
            ),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HabitCream)
                    .border(
                        width = 1.dp,
                        color = HabitBorder,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hydration_watercup_icon),
                    contentDescription = "Water cup",
                    modifier = Modifier.requiredSize(50.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tip",
                    color = HabitTextDark,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Drinking enough water helps you stay\nhealthy and have more energy!",
                    color = HabitTextDark,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun HydrationGoalCard(
    waterCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = RoundedCornerShape(25.dp)
            ),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hydration_goal_icon),
                    contentDescription = "Goal",
                    modifier = Modifier.requiredSize(54.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Today’s Goal",
                    color = HabitTextDark,
                    fontSize = 22.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Drink water 3 times today.",
                color = HabitTextDark,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "Every 5 min you drink, tap the button!",
                color = HabitTextDark,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "Water given : $waterCount/$WATER_GOAL",
                color = HabitTextDark,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(WATER_GOAL) { index ->
                    WaterStepCircle(
                        stepNumber = index + 1,
                        completed = waterCount > index
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterStepCircle(
    stepNumber: Int,
    completed: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(if (completed) HabitLightGreen else HabitCream)
                .border(
                    width = 1.dp,
                    color = if (completed) HabitGreen else HabitBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hydration_waterdrop_icon),
                contentDescription = "Water step $stepNumber",
                modifier = Modifier.requiredSize(39.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$stepNumber",
            color = HabitTextDark,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HydrationActionButton(
    waterCount: Int,
    cooldownSecondsLeft: Int,
    onClick: () -> Unit
) {
    val isCompleted = waterCount >= WATER_GOAL
    val canClick = isCompleted || cooldownSecondsLeft <= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(enabled = canClick) { onClick() },
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canClick) HabitGreen else HabitGreen.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hydration_waterdrop_icon),
                contentDescription = "Water drop",
                modifier = Modifier.requiredSize(36.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        isCompleted -> "Finish Challenge"
                        cooldownSecondsLeft > 0 -> "Wait ${formatCooldown(cooldownSecondsLeft)}"
                        else -> "I drank water!"
                    },
                    color = HabitCard,
                    fontSize = 16.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = when {
                        isCompleted -> "Hydration goal reached"
                        cooldownSecondsLeft > 0 -> "You can drink again soon"
                        else -> "Give water to my buddy"
                    },
                    color = HabitCard,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun HydrationCompletionPopup(
    companionName: String,
    onFinishClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.24f))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = HabitGreen,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hydration_waterdrop_icon),
                    contentDescription = "Hydration complete",
                    modifier = Modifier.requiredSize(56.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hydration complete!",
                    color = HabitTextDark,
                    fontSize = 22.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Great job! You drank water 3 times and helped $companionName too.",
                    color = HabitTextGrey,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Collect XP",
                    onClick = onFinishClick
                )
            }
        }
    }
}

private fun calculateCooldownSeconds(lastDrinkTimeMillis: Long): Int {
    if (lastDrinkTimeMillis <= 0L) {
        return 0
    }

    val elapsedMillis = System.currentTimeMillis() - lastDrinkTimeMillis
    val remainingMillis = HYDRATION_COOLDOWN_MILLIS - elapsedMillis

    return (remainingMillis / 1000L).toInt().coerceAtLeast(0)
}

private fun formatCooldown(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
}

private const val WATER_GOAL = 3
private const val HYDRATION_COOLDOWN_MILLIS = 5 * 60 * 1000L