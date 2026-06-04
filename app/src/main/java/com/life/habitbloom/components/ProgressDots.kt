package com.life.habitbloom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitDarkGreen
import com.life.habitbloom.ui.theme.HabitGreen

@Composable
fun ProgressDots(
    currentPage: Int,
    totalPages: Int = 4,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(if (isSelected) 17.dp else 15.dp)
                    .background(
                        color = if (isSelected) HabitGreen else HabitBorder,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) HabitDarkGreen else HabitDarkGreen.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
            )
        }
    }
}