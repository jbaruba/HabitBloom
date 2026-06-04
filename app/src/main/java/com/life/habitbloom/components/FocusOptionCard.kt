package com.life.habitbloom.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey

@Composable
fun FocusOptionCard(
    focus: FocusType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable { onClick() }
            .border(
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.2.dp,
                    color = if (selected) HabitGreen else HabitBorder
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
                    painter = painterResource(id = focus.iconRes),
                    contentDescription = focus.title,
                    modifier = Modifier.requiredSize(90.dp), // CHANGED: much bigger icon
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = focus.title,
                    color = HabitTextDark,
                    fontSize = 20.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = focus.description,
                    color = HabitTextGrey,
                    fontSize = 15.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(if (selected) HabitGreen else Color.Transparent)
                    .border(
                        width = 1.1.dp,
                        color = if (selected) HabitGreen else HabitBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}