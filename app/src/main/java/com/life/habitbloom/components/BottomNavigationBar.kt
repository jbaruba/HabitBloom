package com.life.habitbloom.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitTextGrey

@Composable
fun BottomNavigationBar(
    selectedItem: String = "Home",
    onHomeClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onBuddyClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = HabitBorder,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavImageItem(
                icon = R.drawable.home_nav1,
                label = "Home",
                active = selectedItem == "Home",
                onClick = onHomeClick
            )

            BottomNavImageItem(
                icon = R.drawable.home_nav2,
                label = "Progress",
                active = selectedItem == "Progress",
                onClick = onProgressClick
            )

            BottomNavImageItem(
                icon = R.drawable.home_nav3,
                label = "Buddy",
                active = selectedItem == "Buddy",
                onClick = onBuddyClick
            )

            BottomNavImageItem(
                icon = R.drawable.home_exp,
                label = "Rewards",
                active = selectedItem == "Rewards",
                onClick = onRewardsClick
            )

            BottomNavEmojiItem(
                icon = "⚙️",
                label = "Settings",
                active = selectedItem == "Settings",
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun BottomNavImageItem(
    icon: Int,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (active) 44.dp else 40.dp)
                .clip(CircleShape)
                .background(
                    if (active) HabitGreen.copy(alpha = 0.16f)
                    else HabitCream.copy(alpha = 0f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.requiredSize(if (active) 66.dp else 62.dp),
                colorFilter = ColorFilter.tint(
                    if (active) HabitGreen else HabitTextGrey
                )
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = if (active) HabitGreen else HabitTextGrey,
            fontSize = 14.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .size(
                    width = if (active) 22.dp else 0.dp,
                    height = 3.dp
                )
                .clip(RoundedCornerShape(50.dp))
                .background(if (active) HabitGreen else HabitCard)
        )
    }
}

@Composable
private fun BottomNavEmojiItem(
    icon: String,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (active) 44.dp else 40.dp)
                .clip(CircleShape)
                .background(
                    if (active) HabitGreen.copy(alpha = 0.16f)
                    else HabitCream.copy(alpha = 0f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = if (active) 28.sp else 26.sp
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = if (active) HabitGreen else HabitTextGrey,
            fontSize = 14.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .size(
                    width = if (active) 22.dp else 0.dp,
                    height = 3.dp
                )
                .clip(RoundedCornerShape(50.dp))
                .background(if (active) HabitGreen else HabitCard)
        )
    }
}