package com.life.habitbloom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitTextDark

@Composable
fun SettingSectionTitle(
    title: String,
    icon: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = title,
            color = HabitTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun SettingRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = HabitTextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "›",
            color = HabitTextDark,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = HabitTextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = HabitCard,
                checkedTrackColor = HabitGreen,
                uncheckedThumbColor = HabitCard,
                uncheckedTrackColor = HabitBorder
            )
        )
    }
}

@Composable
fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HabitBorder.copy(alpha = 0.9f))
    )
}

@Composable
fun SmallCircleIcon(
    text: String
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .border(1.dp, HabitBorder, CircleShape)
            .background(HabitCard, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text
        )
    }
}