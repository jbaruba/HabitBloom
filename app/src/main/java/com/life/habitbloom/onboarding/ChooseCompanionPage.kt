package com.life.habitbloom.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.components.CompanionOptionCard
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.components.ProgressDots
import com.life.habitbloom.components.SecondaryButton
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.ui.theme.*

@Composable
fun ChooseCompanionPage(
    selectedCompanion: CompanionType,
    onCompanionSelected: (CompanionType) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .padding(horizontal = 18.dp)
            .padding(top = 26.dp, bottom = 14.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressDots(currentPage = 1)

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Choose Your companion",
            color = HabitTextDark,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your buddy will grow with you as you build\n healthy habits!",
            color = HabitTextGrey,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(55.dp))

        CompanionType.entries.forEach { companion ->
            CompanionOptionCard(
                companion = companion,
                selected = selectedCompanion == companion,
                onClick = { onCompanionSelected(companion) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = "Continue",
            onClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBackClick
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}