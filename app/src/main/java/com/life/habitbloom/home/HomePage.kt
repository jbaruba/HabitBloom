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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.BottomNavigationBar
import com.life.habitbloom.components.HomeHeroSection
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import java.time.LocalDate

data class DailyChallenge(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val xp: Int,
    val buttonText: String
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
    onSettingsClick: () -> Unit = {}
) {
    val challenges = listOf(
        DailyChallenge(
            icon = R.drawable.home_exercise1,
            title = "Walk for 10 minutes",
            subtitle = "Take a walk with $companionName",
            xp = 50,
            buttonText = "Start"
        ),
        DailyChallenge(
            icon = R.drawable.home_exercise2,
            title = "Drink 3 glasses of water",
            subtitle = "Hydration",
            xp = 20,
            buttonText = "Mark Done"
        ),
        DailyChallenge(
            icon = R.drawable.home_exercise3,
            title = "Go to bed before 11 pm",
            subtitle = "Sleep routine",
            xp = 30,
            buttonText = "Mark Done"
        )
    )

    val completedChallenges = remember { mutableStateListOf<Int>() }

    var totalXp by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var lastStreakDate by remember { mutableStateOf<LocalDate?>(null) }

    val completedCount = completedChallenges.size
    val levelInfo = calculateLevelInfo(totalXp)

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
            // CHANGED:
            // Hero and progress card are inside one Box.
            // This makes real overlap possible.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // CHANGED:
                    // Height is bigger because progress card is moved down with offset.
                    // This prevents Today's Challenges from going behind the progress card.
                    .height(482.dp)
            ) {
                HomeHeroSection(
                    userName = userName,
                    companionName = companionName,
                    companionType = companionType,
                    level = levelInfo.level,
                    stage = levelInfo.stage,
                    streak = streak
                )

                DailyProgressCard(
                    level = levelInfo.level,
                    currentXp = levelInfo.currentLevelXp,
                    maxXp = levelInfo.xpNeededForNextLevel,
                    progress = levelInfo.progress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp)
                        // CHANGED:
                        // Positive offset puts the card lower.
                        // This is your chosen position.
                        .offset(y = -9.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                // CHANGED:
                // Adds real space after the progress card.
                // This prevents overlap with Today's Challenges.
                Spacer(modifier = Modifier.height(15.dp))

                TodayHeader(completedCount = completedCount)

                Spacer(modifier = Modifier.height(10.dp))

                challenges.forEachIndexed { index, challenge ->
                    ChallengeRow(
                        challenge = challenge,
                        completed = completedChallenges.contains(index),
                        locked = false,
                        onClick = {
                            if (!completedChallenges.contains(index)) {
                                completedChallenges.add(index)
                                totalXp += challenge.xp

                                val today = LocalDate.now()

                                if (lastStreakDate != today) {
                                    streak += 1
                                    lastStreakDate = today
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                BonusChallengeRow(
                    unlocked = completedCount == 3
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

private fun calculateLevelInfo(totalXp: Int): LevelInfo {
    val levelThresholds = listOf(0, 50, 150, 300, 500, 800)

    val level = when {
        totalXp < levelThresholds[1] -> 0
        totalXp < levelThresholds[2] -> 1
        totalXp < levelThresholds[3] -> 2
        totalXp < levelThresholds[4] -> 3
        totalXp < levelThresholds[5] -> 4
        else -> 5
    }

    val currentLevelStartXp = levelThresholds.getOrElse(level) { levelThresholds.last() }
    val nextLevelXp = levelThresholds.getOrElse(level + 1) { levelThresholds.last() }

    val currentLevelXp = (totalXp - currentLevelStartXp).coerceAtLeast(0)
    val xpNeededForNextLevel = (nextLevelXp - currentLevelStartXp).coerceAtLeast(1)
    val progress = (currentLevelXp.toFloat() / xpNeededForNextLevel.toFloat()).coerceIn(0f, 1f)
    val stage = (level + 1).coerceIn(1, 3)

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
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = RoundedCornerShape(20.dp)
            ),
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
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
private fun TodayHeader(
    completedCount: Int
) {
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
            text = "$completedCount/3 challenges complete",
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
                        if (index < completedCount) HabitGreen else HabitBorder.copy(alpha = 0.45f)
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
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = RoundedCornerShape(18.dp)
            ),
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
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

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (completed) "Done ✓" else "+ ${challenge.xp} XP ⭐",
                    color = HabitGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.clickable(enabled = !completed && !locked) { onClick() },
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
    unlocked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = RoundedCornerShape(18.dp)
            ),
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
                    painter = painterResource(id = R.drawable.home_exercise4),
                    contentDescription = "Bonus Challenge",
                    modifier = Modifier.requiredSize(54.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Bonus Challenge",
                    color = HabitGreen,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "5 min breathing exercise",
                    color = HabitTextDark,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (unlocked) {
                        "Unlocked bonus challenge"
                    } else {
                        "Unlock after completing all 3 daily challenges"
                    },
                    color = HabitTextGrey,
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = if (unlocked) "+ 30 XP ⭐" else "🔒",
                color = if (unlocked) HabitGreen else HabitTextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}