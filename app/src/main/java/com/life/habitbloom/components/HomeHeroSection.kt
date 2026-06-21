package com.life.habitbloom.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitDarkGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun HomeHeroSection(
    userName: String,
    companionName: String,
    companionType: CompanionType,
    level: Int,
    stage: Int,
    streak: Int
) {
    var greeting by remember { mutableStateOf(getGreetingMessage()) }

    LaunchedEffect(Unit) {
        while (true) {
            greeting = getGreetingMessage()
            delay(60_000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(392.dp)
    ) {
        HomeHeroTopBar(
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.progress_background),
                contentDescription = "Hero background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$greeting, $userName!",
                        color = HabitTextDark,
                        fontSize = 22.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Image(
                        painter = painterResource(id = R.drawable.home_sun),
                        contentDescription = "Sun",
                        modifier = Modifier.size(22.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "Let’s grow healthy habits together.",
                    color = HabitTextGrey,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedCompanionImage(
                companionName = companionName,
                companionType = companionType,
                stage = stage,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 6.dp)
            )

            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 28.dp)
                    .offset(y = (-18).dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HabitCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "I’m ready! 💗",
                    color = HabitTextDark,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            Text(
                text = companionName,
                color = HabitDarkGreen,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 58.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 18.dp, end = 18.dp, bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeHeroChip(
                    icon = R.drawable.home_stage_plant,
                    text = "Stage $stage"
                )

                Spacer(modifier = Modifier.weight(1f))

                HomeHeroChip(
                    icon = R.drawable.home_streak,
                    text = "$streak day streak"
                )
            }
        }
    }
}

@Composable
private fun AnimatedCompanionImage(
    companionName: String,
    companionType: CompanionType,
    stage: Int,
    modifier: Modifier = Modifier
) {
    var displayedStage by remember(companionType) { mutableIntStateOf(stage) }
    var isChanging by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isChanging) 0.72f else 1f,
        animationSpec = tween(durationMillis = 650),
        label = "companionScale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isChanging) 0.25f else 1f,
        animationSpec = tween(durationMillis = 650),
        label = "companionAlpha"
    )

    val animatedRotation by animateFloatAsState(
        targetValue = if (isChanging) -4f else 0f,
        animationSpec = tween(durationMillis = 650),
        label = "companionRotation"
    )

    LaunchedEffect(stage, companionType) {
        if (displayedStage != stage) {
            isChanging = true
            delay(420)
            displayedStage = stage
            delay(120)
            isChanging = false
        }
    }

    Image(
        painter = painterResource(id = companionType.imageForStage(displayedStage)),
        contentDescription = companionName,
        modifier = modifier
            .requiredSize(
                when (companionType.title) {
                    "Seed" -> 240.dp
                    "Puppy" -> 250.dp
                    "Kitten" -> 245.dp
                    else -> 240.dp
                }
            )
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
                rotationZ = animatedRotation
            },
        contentScale = ContentScale.Fit
    )
}

private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
private fun HomeHeroTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(60.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_title),
                contentDescription = "Grow Daily",
                modifier = Modifier
                    .requiredSize(150.dp)
                    .graphicsLayer(
                        scaleX = 1.45f,
                        scaleY = 1.45f
                    ),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TopIconCircle()
    }
}

@Composable
private fun TopIconCircle() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(HabitCard)
            .border(
                width = 1.dp,
                color = HabitBorder,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔔",
            fontSize = 22.sp
        )
    }
}

@Composable
private fun HomeHeroChip(
    icon: Int,
    text: String
) {
    Card(
        modifier = Modifier.border(
            width = 1.1.dp,
            color = HabitTextDark.copy(alpha = 0.22f),
            shape = RoundedCornerShape(50.dp)
        ),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .padding(start = 12.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = text,
                    modifier = Modifier.requiredSize(66.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = HabitDarkGreen,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}