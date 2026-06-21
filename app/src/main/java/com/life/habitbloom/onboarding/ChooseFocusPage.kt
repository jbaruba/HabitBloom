package com.life.habitbloom.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.life.habitbloom.components.FocusOptionCard
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.components.ProgressDots
import com.life.habitbloom.components.SecondaryButton
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.ui.theme.*

@Composable
fun ChooseFocusPage(
    selectedFocuses: SnapshotStateList<FocusType>,
    onFocusToggle: (FocusType) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val canContinue = selectedFocuses.size == 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .padding(horizontal = 18.dp)
            .padding(top = 28.dp, bottom = 14.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressDots(currentPage = 2)

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Choose your focus",
            color = HabitTextDark,
            fontSize = 35.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select exactly 3 habit areas before you continue.",
            color = HabitTextGrey,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${selectedFocuses.size}/3 selected",
            color = if (canContinue) HabitGreen else HabitTextGrey,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        FocusType.entries.forEach { focus ->
            FocusOptionCard(
                focus = focus,
                selected = selectedFocuses.contains(focus),
                onClick = { onFocusToggle(focus) }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        GoodToKnowCard()

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "Continue",
            onClick = onContinueClick,
            enabled = canContinue
        )

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}

@Composable
private fun GoodToKnowCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .border(
                border = BorderStroke(
                    width = 1.2.dp,
                    color = HabitBorder
                ),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(HabitGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.page3_icon5),
                    contentDescription = "Good to know",
                    modifier = Modifier.requiredSize(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Good to know",
                    color = HabitTextDark,
                    fontSize = 20.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your daily challenges will be based on your selected focus areas.",
                    color = HabitTextGrey,
                    fontSize = 15.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}