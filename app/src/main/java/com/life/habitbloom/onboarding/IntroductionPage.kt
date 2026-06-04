package com.life.habitbloom.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.components.ProgressDots
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitTextDark

@Composable
fun IntroductionPage(
    onGetStartedClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IntroductionHeader()

        Spacer(modifier = Modifier.height(12.dp))

        HeroPetSection()

        Spacer(modifier = Modifier.height((-20).dp))

        HowItWorksCard(
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryButton(
            text = "Get Started",
            onClick = onGetStartedClick,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun IntroductionHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        ProgressDots(
            currentPage = 0,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.habitbloom_name),
            contentDescription = "HabitBloom logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(400.dp)
                .height(200.dp)
                .offset(y = (-10).dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Build healthy habits with small daily steps\nand watch your buddy grow!",
            color = HabitTextDark,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 108.dp)
                .fillMaxWidth(0.92f)
        )
    }
}

@Composable
private fun HeroPetSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.page1_background),
            contentDescription = "Nature background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.dog_3),
            contentDescription = "Puppy",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(185.dp)
                .offset(x = (-8).dp, y = 8.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.plant_3),
            contentDescription = "Seed companion",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(190.dp)
                .offset(x = 15.dp,y = 10.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.cat_3),
            contentDescription = "Kitten",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(230.dp)
                .offset(x = 30.dp, y = 30.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun HowItWorksCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(245.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.3.dp,
            color = HabitTextDark.copy(alpha = 0.24f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // CHANGED: small side padding gives more text space
                .padding(horizontal = 1.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How it works",
                color = HabitTextDark,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                HowItWorksItem(
                    icon = R.drawable.page1_icon1,
                    title = "1. Choose\ncompanion",
                    subtitle = "Pick a buddy to\ngrow with you"
                )

                HowItWorksItem(
                    icon = R.drawable.page1_icon2,
                    title = "2. Complete\nchallenges",
                    subtitle = "Build habits\none day at a time"
                )

                HowItWorksItem(
                    icon = R.drawable.page1_icon3,
                    title = "3. Help buddy\ngrow",
                    subtitle = "Stay consistent and\nwatch them thrive!"
                )
            }
        }
    }
}

@Composable
private fun HowItWorksItem(
    icon: Int,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .background(
                    color = HabitCream,
                    shape = CircleShape
                )
                .border(
                    width = 1.2.dp,
                    color = HabitTextDark.copy(alpha = 0.18f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // CHANGED:
            // requiredSize + graphicsLayer makes the icon visually bigger.
            // This helps when the png/vector asset has empty transparent space.
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier
                    .requiredSize(40.dp)
                    .graphicsLayer(
                        scaleX = 2.4f,
                        scaleY = 2.4f
                    ),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = title,
            color = HabitTextDark,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = subtitle,
            color = HabitTextDark.copy(alpha = 0.82f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}