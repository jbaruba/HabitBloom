package com.life.habitbloom.progress

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.BottomNavigationBar
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

data class ProgressMetric(
    val icon: Int,
    val title: String,
    val value: String,
    val color: Color
)

data class HabitProgressItem(
    val focusType: FocusType,
    val icon: Int,
    val title: String,
    val value: String,
    val completed: Int,
    val target: Int,
    val progress: Float,
    val color: Color
)

data class ProgressLevelInfo(
    val level: Int,
    val currentLevelXp: Int,
    val xpNeededForNextLevel: Int,
    val progress: Float
)

@Composable
fun ProgressPage(
    totalXp: Int,
    streak: Int,
    focusTypes: List<FocusType>,
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    var selectedHabit by remember { mutableStateOf(resolveDefaultHabit(focusTypes)) }

    val levelInfo = calculateProgressLevel(totalXp)

    val habitItems = buildHabitProgressItems(
        focusTypes = focusTypes,
        totalXp = totalXp,
        streak = streak
    )

    val selectedHabitItem = habitItems.firstOrNull { it.title == selectedHabit }
        ?: habitItems.first()

    val challengesDone = habitItems.sumOf { it.completed }
    val longestStreak = streak

    val metrics = listOf(
        ProgressMetric(
            icon = R.drawable.progress_lotus,
            title = "Current\nStreak",
            value = "$streak days",
            color = Color(0xFFFF7E3F)
        ),
        ProgressMetric(
            icon = R.drawable.progress_star,
            title = "Longest\nStreak",
            value = "$longestStreak days",
            color = Color(0xFFF5B942)
        ),
        ProgressMetric(
            icon = R.drawable.progress_corner_image,
            title = "Total XP",
            value = "$totalXp XP",
            color = HabitGreen
        ),
        ProgressMetric(
            icon = R.drawable.progress_trofy,
            title = "Challenges\nDone",
            value = "$challengesDone",
            color = Color(0xFF9B78FF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressHeader()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                metrics.forEach { metric ->
                    ProgressMetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LevelProgressCard(
                level = levelInfo.level,
                currentXp = levelInfo.currentLevelXp,
                maxXp = levelInfo.xpNeededForNextLevel,
                progress = levelInfo.progress
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProgressFilterRow(
                selectedPeriod = selectedPeriod,
                selectedHabit = selectedHabit,
                habitItems = habitItems,
                onPeriodSelected = { selectedPeriod = it },
                onHabitClick = {
                    selectedHabit = nextHabitName(
                        currentHabit = selectedHabit,
                        habitItems = habitItems
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProgressChartCard(
                selectedPeriod = selectedPeriod,
                selectedHabitItem = selectedHabitItem
            )

            Spacer(modifier = Modifier.height(10.dp))

            HabitProgressGrid(
                items = habitItems,
                selectedHabit = selectedHabit,
                onHabitSelected = { selectedHabit = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        BottomNavigationBar(
            selectedItem = "Progress",
            onHomeClick = onHomeClick,
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
private fun ProgressHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 18.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_title),
                contentDescription = "Grow Daily",
                modifier = Modifier.requiredSize(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your Progress",
                color = HabitTextDark,
                fontSize = 29.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "See how your habits are growing.",
                color = HabitTextGrey,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Image(
            painter = painterResource(id = R.drawable.progress_corner_image),
            contentDescription = "Progress decoration",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .requiredSize(170.dp)
                .offset(x = 8.dp, y = 0.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.progress_plant),
            contentDescription = "Progress plant",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 34.dp, bottom = 14.dp)
                .requiredSize(100.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ProgressMetricCard(
    metric: ProgressMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(122.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(metric.color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = metric.icon),
                    contentDescription = metric.title,
                    modifier = Modifier.requiredSize(46.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = metric.title,
                color = HabitTextDark,
                fontSize = 10.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = metric.value,
                color = metric.color,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LevelProgressCard(
    level: Int,
    currentXp: Int,
    maxXp: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(62.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_level),
                    contentDescription = "Level",
                    modifier = Modifier.requiredSize(74.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Level\n$level",
                    color = Color.White,
                    fontSize = 9.sp,
                    lineHeight = 9.sp,
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
                    color = HabitTextDark,
                    fontSize = 14.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProgressBar(
                    progress = progress,
                    color = HabitGreen,
                    height = 11
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.progress_plant),
                contentDescription = "Growing plant",
                modifier = Modifier.requiredSize(68.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ProgressFilterRow(
    selectedPeriod: String,
    selectedHabit: String,
    habitItems: List<HabitProgressItem>,
    onPeriodSelected: (String) -> Unit,
    onHabitClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Week", "Month", "Year").forEach { period ->
            FilterChip(
                text = period,
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier
                .height(40.dp)
                .clickable { onHabitClick() }
                .border(1.dp, HabitBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val selectedItem = habitItems.firstOrNull { it.title == selectedHabit }

                if (selectedItem != null) {
                    Image(
                        painter = painterResource(id = selectedItem.icon),
                        contentDescription = selectedItem.title,
                        modifier = Modifier.requiredSize(30.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                }

                Text(
                    text = selectedHabit,
                    color = HabitTextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "⌄",
                    color = HabitTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
            .border(1.dp, HabitBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) HabitCard else HabitCream
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = HabitTextDark,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProgressChartCard(
    selectedPeriod: String,
    selectedHabitItem: HabitProgressItem
) {
    val values = remember(selectedPeriod, selectedHabitItem.title, selectedHabitItem.completed) {
        buildChartValues(
            selectedPeriod = selectedPeriod,
            currentProgress = selectedHabitItem.progress
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = selectedHabitItem.icon),
                    contentDescription = selectedHabitItem.title,
                    modifier = Modifier.requiredSize(34.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${selectedHabitItem.title} progress",
                    color = HabitTextDark,
                    fontSize = 16.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = selectedHabitItem.value,
                    color = selectedHabitItem.color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .width(30.dp)
                        .fillMaxHeight()
                        .padding(top = 5.dp, bottom = 26.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    listOf("100", "75", "50", "25", "0").forEach { label ->
                        Text(
                            text = label,
                            color = HabitTextGrey,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val chartLeft = 0f
                        val chartRight = size.width - 4.dp.toPx()
                        val chartTop = 6.dp.toPx()
                        val chartBottom = size.height - 28.dp.toPx()

                        val axisColor = Color(0xFFE1DED2)
                        val lineColor = selectedHabitItem.color

                        repeat(5) { index ->
                            val y = chartTop + ((chartBottom - chartTop) / 4f) * index

                            drawLine(
                                color = axisColor,
                                start = Offset(chartLeft, y),
                                end = Offset(chartRight, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        drawLine(
                            color = axisColor,
                            start = Offset(chartLeft, chartTop),
                            end = Offset(chartLeft, chartBottom),
                            strokeWidth = 1.dp.toPx()
                        )

                        drawLine(
                            color = axisColor,
                            start = Offset(chartLeft, chartBottom),
                            end = Offset(chartRight, chartBottom),
                            strokeWidth = 1.dp.toPx()
                        )

                        val stepX = (chartRight - chartLeft) / (values.size - 1)

                        val points = values.mapIndexed { index, value ->
                            val x = chartLeft + stepX * index
                            val y = chartBottom - ((value / 100f) * (chartBottom - chartTop))
                            Offset(x, y)
                        }

                        val path = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points.first().x, points.first().y)

                                for (index in 1 until points.size) {
                                    lineTo(points[index].x, points[index].y)
                                }
                            }
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        points.forEach { point ->
                            drawCircle(
                                color = HabitCard,
                                radius = 5.dp.toPx(),
                                center = point
                            )

                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        buildChartLabels(selectedPeriod).forEach { label ->
                            Text(
                                text = label,
                                color = HabitTextGrey,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitProgressGrid(
    items: List<HabitProgressItem>,
    selectedHabit: String,
    onHabitSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            HabitProgressCard(
                item = item,
                selected = item.title == selectedHabit,
                modifier = Modifier.weight(1f),
                onClick = { onHabitSelected(item.title) }
            )
        }
    }
}

@Composable
private fun HabitProgressCard(
    item: HabitProgressItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(152.dp)
            .clickable { onClick() }
            .border(
                width = if (selected) 1.8.dp else 1.dp,
                color = if (selected) item.color else HabitBorder,
                shape = RoundedCornerShape(17.dp)
            ),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    modifier = Modifier.requiredSize(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = item.color,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = item.value,
                color = HabitTextDark,
                fontSize = 16.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            ProgressBar(
                progress = item.progress,
                color = item.color,
                height = 8
            )
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    color: Color,
    height: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(HabitBorder.copy(alpha = 0.65f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(color)
        )
    }
}

private fun buildHabitProgressItems(
    focusTypes: List<FocusType>,
    totalXp: Int,
    streak: Int
): List<HabitProgressItem> {
    val selectedFocusTypes = focusTypes.toSet()

    fun isSelected(focusType: FocusType): Boolean {
        return selectedFocusTypes.isEmpty() || selectedFocusTypes.contains(focusType)
    }

    fun completedFor(
        focusType: FocusType,
        xpPerCompletion: Int,
        useStreak: Boolean = false
    ): Int {
        if (!isSelected(focusType)) {
            return 0
        }

        if (useStreak) {
            return streak.coerceIn(0, HABIT_TARGET)
        }

        return (totalXp / xpPerCompletion).coerceIn(0, HABIT_TARGET)
    }

    val movementDone = completedFor(
        focusType = FocusType.MOVEMENT,
        xpPerCompletion = 50
    )

    val hydrationDone = completedFor(
        focusType = FocusType.HYDRATION,
        xpPerCompletion = 20
    )

    val sleepDone = completedFor(
        focusType = FocusType.SLEEP,
        xpPerCompletion = 30,
        useStreak = true
    )

    val relaxationDone = completedFor(
        focusType = FocusType.RELAXATION,
        xpPerCompletion = 30
    )

    return listOf(
        HabitProgressItem(
            focusType = FocusType.MOVEMENT,
            icon = R.drawable.page3_icon1,
            title = "Movement",
            value = "$movementDone/$HABIT_TARGET",
            completed = movementDone,
            target = HABIT_TARGET,
            progress = movementDone.toFloat() / HABIT_TARGET.toFloat(),
            color = HabitGreen
        ),
        HabitProgressItem(
            focusType = FocusType.HYDRATION,
            icon = R.drawable.hydration_waterdrop_icon,
            title = "Hydration",
            value = "$hydrationDone/$HABIT_TARGET",
            completed = hydrationDone,
            target = HABIT_TARGET,
            progress = hydrationDone.toFloat() / HABIT_TARGET.toFloat(),
            color = Color(0xFF3498DB)
        ),
        HabitProgressItem(
            focusType = FocusType.SLEEP,
            icon = R.drawable.sleep_sleep_icon,
            title = "Sleep",
            value = "$sleepDone/$HABIT_TARGET",
            completed = sleepDone,
            target = HABIT_TARGET,
            progress = sleepDone.toFloat() / HABIT_TARGET.toFloat(),
            color = Color(0xFF8B6CFF)
        ),
        HabitProgressItem(
            focusType = FocusType.RELAXATION,
            icon = R.drawable.progress_lotus,
            title = "Relaxation",
            value = "$relaxationDone/$HABIT_TARGET",
            completed = relaxationDone,
            target = HABIT_TARGET,
            progress = relaxationDone.toFloat() / HABIT_TARGET.toFloat(),
            color = Color(0xFFFF8A3D)
        )
    )
}

private fun calculateProgressLevel(totalXp: Int): ProgressLevelInfo {
    val levelThresholds = listOf(0, 50, 150, 300, 500, 800, 1100)

    val level = when {
        totalXp < 50 -> 0
        totalXp < 150 -> 1
        totalXp < 300 -> 2
        totalXp < 500 -> 3
        totalXp < 800 -> 4
        totalXp < 1100 -> 5
        else -> 6
    }

    val currentLevelStartXp = levelThresholds.getOrElse(level) { levelThresholds.last() }
    val nextLevelXp = levelThresholds.getOrElse(level + 1) { levelThresholds.last() }

    val currentLevelXp = (totalXp - currentLevelStartXp).coerceAtLeast(0)
    val xpNeededForNextLevel = (nextLevelXp - currentLevelStartXp).coerceAtLeast(1)
    val progress = (currentLevelXp.toFloat() / xpNeededForNextLevel.toFloat()).coerceIn(0f, 1f)

    return ProgressLevelInfo(
        level = level,
        currentLevelXp = currentLevelXp,
        xpNeededForNextLevel = xpNeededForNextLevel,
        progress = progress
    )
}

private fun buildChartValues(
    selectedPeriod: String,
    currentProgress: Float
): List<Float> {
    val currentPercent = (currentProgress * 100f).coerceIn(0f, 100f)

    if (currentPercent <= 0f) {
        return List(7) { 0f }
    }

    val multipliers = when (selectedPeriod) {
        "Month" -> listOf(0.10f, 0.20f, 0.32f, 0.45f, 0.60f, 0.78f, 1.00f)
        "Year" -> listOf(0.05f, 0.12f, 0.24f, 0.38f, 0.56f, 0.75f, 1.00f)
        else -> listOf(0.08f, 0.18f, 0.30f, 0.46f, 0.62f, 0.80f, 1.00f)
    }

    return multipliers.map { multiplier ->
        (currentPercent * multiplier).coerceIn(0f, 100f)
    }
}

private fun buildChartLabels(selectedPeriod: String): List<String> {
    return when (selectedPeriod) {
        "Month" -> buildPreviousWeekLabels()
        "Year" -> buildPreviousMonthLabels()
        else -> buildPreviousDayLabels()
    }
}

private fun buildPreviousDayLabels(): List<String> {
    val formatter = SimpleDateFormat("MMM d", Locale.ENGLISH)
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.DAY_OF_YEAR, -6)

    return List(7) {
        val label = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        label
    }
}

private fun buildPreviousWeekLabels(): List<String> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, -6)

    return List(7) {
        val label = "W${calendar.get(Calendar.WEEK_OF_YEAR)}"
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        label
    }
}

private fun buildPreviousMonthLabels(): List<String> {
    val formatter = SimpleDateFormat("MMM", Locale.ENGLISH)
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.MONTH, -6)

    return List(7) {
        val label = formatter.format(calendar.time)
        calendar.add(Calendar.MONTH, 1)
        label
    }
}

private fun resolveDefaultHabit(focusTypes: List<FocusType>): String {
    return when (focusTypes.firstOrNull()) {
        FocusType.MOVEMENT -> "Movement"
        FocusType.HYDRATION -> "Hydration"
        FocusType.SLEEP -> "Sleep"
        FocusType.RELAXATION -> "Relaxation"
        null -> "Movement"
    }
}

private fun nextHabitName(
    currentHabit: String,
    habitItems: List<HabitProgressItem>
): String {
    val names = habitItems.map { it.title }
    val currentIndex = names.indexOf(currentHabit)

    return if (currentIndex == -1 || currentIndex == names.lastIndex) {
        names.first()
    } else {
        names[currentIndex + 1]
    }
}

private const val HABIT_TARGET = 25