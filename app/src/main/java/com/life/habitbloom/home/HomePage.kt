package com.life.habitbloom.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.BottomNavigationBar
import com.life.habitbloom.components.HomeHeroSection
import com.life.habitbloom.data.UserPreferences
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey

data class DailyChallenge(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val xp: Int,
    val buttonText: String,
    val focusType: FocusType
)

data class LevelInfo(
    val level: Int,
    val stage: Int,
    val currentLevelXp: Int,
    val xpNeededForNextLevel: Int,
    val progress: Float
)

@Composable
fun HomePage(
    userName: String,
    companionName: String,
    companionType: CompanionType,
    focusTypes: List<FocusType>,
    completedChallengeFocus: FocusType? = null,
    onCompletedChallengeHandled: () -> Unit = {},
    onChallengeClick: (FocusType) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val today = userPreferences.today()
    val savedDate = userPreferences.getCompletedDate()

    val completedChallenges = remember {
        mutableStateListOf<Int>().apply {
            if (savedDate == today) {
                addAll(userPreferences.getCompletedChallenges())
            }
        }
    }

    val totalXp = remember {
        androidx.compose.runtime.mutableIntStateOf(userPreferences.getTotalXp())
    }

    val streak = remember {
        androidx.compose.runtime.mutableIntStateOf(userPreferences.getStreak())
    }

    val challenges = remember(focusTypes, companionName) {
        buildDailyChallenges(
            selectedFocusTypes = focusTypes,
            companionName = companionName
        )
    }

    val completedDailyCount = completedChallenges.count { it != BONUS_CHALLENGE_ID }
    val levelInfo = calculateLevelInfo(totalXp.intValue)

    fun saveHomeState() {
        userPreferences.saveHomeState(
            totalXp = totalXp.intValue,
            streak = streak.intValue,
            completedDate = today,
            completedChallenges = completedChallenges.toSet()
        )
    }

    fun completeChallengeByFocus(focusType: FocusType) {
        val challengeIndex = challenges.indexOfFirst { it.focusType == focusType }

        if (challengeIndex == -1) {
            return
        }

        if (completedChallenges.contains(challengeIndex)) {
            return
        }

        if (completedDailyCount >= 3) {
            return
        }

        completedChallenges.add(challengeIndex)
        totalXp.intValue += challenges[challengeIndex].xp

        val newCompletedDailyCount = completedChallenges.count { it != BONUS_CHALLENGE_ID }

        if (newCompletedDailyCount == 1) {
            streak.intValue += 1
        }

        saveHomeState()
    }

    LaunchedEffect(completedChallengeFocus) {
        completedChallengeFocus?.let { focusType ->
            completeChallengeByFocus(focusType)
            onCompletedChallengeHandled()
        }
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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(482.dp)
            ) {
                HomeHeroSection(
                    userName = userName,
                    companionName = companionName,
                    companionType = companionType,
                    level = levelInfo.level,
                    stage = levelInfo.stage,
                    streak = streak.intValue
                )

                DailyProgressCard(
                    level = levelInfo.level,
                    currentXp = levelInfo.currentLevelXp,
                    maxXp = levelInfo.xpNeededForNextLevel,
                    progress = levelInfo.progress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp)
                        .offset(y = 9.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(15.dp))

                TodayHeader(completedCount = completedDailyCount)

                Spacer(modifier = Modifier.height(10.dp))

                challenges.forEachIndexed { index, challenge ->
                    ChallengeRow(
                        challenge = challenge,
                        completed = completedChallenges.contains(index),
                        locked = false,
                        onClick = {
                            if (!completedChallenges.contains(index) && completedDailyCount < 3) {
                                onChallengeClick(challenge.focusType)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                BonusChallengeRow(
                    unlocked = completedDailyCount == 3,
                    completed = completedChallenges.contains(BONUS_CHALLENGE_ID),
                    onClick = {
                        if (completedDailyCount == 3 && !completedChallenges.contains(BONUS_CHALLENGE_ID)) {
                            completedChallenges.add(BONUS_CHALLENGE_ID)
                            totalXp.intValue += BONUS_XP_REWARD
                            saveHomeState()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        BottomNavigationBar(
            selectedItem = "Home",
            onSettingsClick = onSettingsClick
        )
    }
}

private fun buildDailyChallenges(
    selectedFocusTypes: List<FocusType>,
    companionName: String
): List<DailyChallenge> {
    val selected = selectedFocusTypes.take(3)

    return selected.map { focus ->
        when (focus) {
            FocusType.MOVEMENT -> DailyChallenge(
                icon = R.drawable.home_exercise1,
                title = "Reach your step goal",
                subtitle = "Track real steps with $companionName",
                xp = 50,
                buttonText = "Start",
                focusType = FocusType.MOVEMENT
            )

            FocusType.HYDRATION -> DailyChallenge(
                icon = R.drawable.home_exercise2,
                title = "Drink water 3 times",
                subtitle = "Drink water and water your buddy",
                xp = 20,
                buttonText = "Start",
                focusType = FocusType.HYDRATION
            )

            FocusType.SLEEP -> DailyChallenge(
                icon = R.drawable.home_exercise3,
                title = "Reach your sleep goal",
                subtitle = "Track phone-free time at night",
                xp = 30,
                buttonText = "Start",
                focusType = FocusType.SLEEP
            )

            FocusType.RELAXATION -> DailyChallenge(
                icon = R.drawable.home_exercise4,
                title = "5 min breathing exercise",
                subtitle = "Relax and reset",
                xp = 30,
                buttonText = "Start",
                focusType = FocusType.RELAXATION
            )
        }
    }
}

private fun calculateLevelInfo(totalXp: Int): LevelInfo {
    val levelThresholds = listOf(0, 50, 150, 300, 500, 800, 1100)

    val level = when {
        totalXp < 50 -> 0
        totalXp < 150 -> 1
        totalXp < 300 -> 2
        totalXp < 500 -> 3
        totalXp < 800 -> 4
        totalXp < 1100 -> 5
        else -> 6
    }

    val currentLevelStartXp = levelThresholds.getOrElse(level) { levelThresholds.last() }
    val nextLevelXp = levelThresholds.getOrElse(level + 1) { levelThresholds.last() }

    val currentLevelXp = (totalXp - currentLevelStartXp).coerceAtLeast(0)
    val xpNeededForNextLevel = (nextLevelXp - currentLevelStartXp).coerceAtLeast(1)
    val progress = (currentLevelXp.toFloat() / xpNeededForNextLevel.toFloat()).coerceIn(0f, 1f)
    val stage = (level + 1).coerceIn(1, 6)

    return LevelInfo(
        level = level,
        stage = stage,
        currentLevelXp = currentLevelXp,
        xpNeededForNextLevel = xpNeededForNextLevel,
        progress = progress
    )
}

@Composable
private fun DailyProgressCard(
    level: Int,
    currentXp: Int,
    maxXp: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(78.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_level),
                    contentDescription = "Level",
                    modifier = Modifier.requiredSize(120.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Level\n$level",
                    color = HabitCard,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$currentXp / $maxXp XP",
                    color = HabitTextGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(HabitBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(9.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(HabitGreen)
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayHeader(completedCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🌱 Today’s Challenges",
            color = HabitTextDark,
            fontSize = 17.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$completedCount/3",
            color = HabitTextGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(6.dp))

        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < completedCount.coerceAtMost(3)) {
                            HabitGreen
                        } else {
                            HabitBorder.copy(alpha = 0.45f)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun ChallengeRow(
    challenge: DailyChallenge,
    completed: Boolean,
    locked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
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
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(HabitLightGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = challenge.icon),
                    contentDescription = challenge.title,
                    modifier = Modifier.requiredSize(52.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    color = HabitTextDark,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = challenge.subtitle,
                    color = HabitTextGrey,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (completed) "Done ✓" else "+ ${challenge.xp} XP ⭐",
                    color = HabitGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.clickable(enabled = !completed && !locked) {
                        onClick()
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (completed) HabitBorder else HabitGreen
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (completed) "Done" else challenge.buttonText,
                        color = HabitCard,
                        fontSize = 11.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BonusChallengeRow(
    unlocked: Boolean,
    completed: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) HabitCard else HabitBorder.copy(alpha = 0.22f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) HabitLightGreen else HabitBorder.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unlocked) "🎁" else "🔒",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bonus Challenge",
                    color = HabitTextDark,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (unlocked) {
                        "Complete this bonus for extra XP"
                    } else {
                        "Complete all 3 challenges to unlock"
                    },
                    color = HabitTextGrey,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (completed) "Done ✓" else "+ $BONUS_XP_REWARD XP ⭐",
                    color = if (unlocked) HabitGreen else HabitTextGrey,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.clickable(enabled = unlocked && !completed) {
                        onClick()
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            completed -> HabitBorder
                            unlocked -> HabitGreen
                            else -> HabitBorder.copy(alpha = 0.55f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = when {
                            completed -> "Done"
                            unlocked -> "Claim"
                            else -> "Locked"
                        },
                        color = HabitCard,
                        fontSize = 11.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

private const val BONUS_CHALLENGE_ID = 99
private const val BONUS_XP_REWARD = 30