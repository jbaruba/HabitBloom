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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey

@Composable
fun CompanionOptionCard(
    companion: CompanionType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(128.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = companion.imageRes),
                        contentDescription = companion.title,
                        modifier = Modifier
                            .requiredSize(
                                when (companion.title) {
                                    "Seed" -> 146.dp
                                    "Puppy" -> 138.dp
                                    "Kitten" -> 145.dp
                                    else -> 140.dp
                                }
                            )
                            .offset(
                                x = when (companion.title) {
                                    "Seed" -> (-6).dp
                                    "Puppy" -> (-2).dp
                                    "Kitten" -> (-2).dp
                                    else -> 0.dp
                                },
                                y = when (companion.title) {
                                    "Seed" -> 2.dp
                                    "Puppy" -> 4.dp
                                    "Kitten" -> 4.dp
                                    else -> 0.dp
                                }
                            ),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = companion.title,
                        color = HabitTextDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = companion.description,
                        color = HabitTextGrey,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 18.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (selected) HabitGreen else Color.Transparent)
                    .border(
                        width = 1.dp,
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