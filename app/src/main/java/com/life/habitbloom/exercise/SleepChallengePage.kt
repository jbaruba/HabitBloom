package com.life.habitbloom.exercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.life.habitbloom.R
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import java.util.Calendar
import kotlin.math.max

@Composable
fun SleepChallengePage(
    companionName: String,
    bedtimeGoal: String = "11:00 PM",
    sleepGoalHours: Int = 8,
    onBackClick: () -> Unit,
    onChallengeCompleted: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val prefs = remember {
        context.getSharedPreferences(SLEEP_PREFS, Context.MODE_PRIVATE)
    }

    val today = remember { currentDayOfYear() }

    LaunchedEffect(Unit) {
        val savedDay = prefs.getInt(KEY_SLEEP_DAY, -1)

        if (savedDay != today) {
            prefs.edit()
                .putInt(KEY_SLEEP_DAY, today)
                .putBoolean(KEY_SLEEP_MODE_STARTED, false)
                .putBoolean(KEY_SLEEP_XP_COLLECTED, false)
                .putLong(KEY_SLEEP_SESSION_START, 0L)
                .putLong(KEY_SCREEN_OFF_START, 0L)
                .putInt(KEY_SLEEP_LOCKED_MINUTES, 0)
                .apply()
        }
    }

    var sleepModeStarted by remember {
        mutableStateOf(prefs.getBoolean(KEY_SLEEP_MODE_STARTED, false))
    }

    var lockedMinutes by remember {
        mutableIntStateOf(prefs.getInt(KEY_SLEEP_LOCKED_MINUTES, 0))
    }

    var sessionStartTime by remember {
        mutableLongStateOf(prefs.getLong(KEY_SLEEP_SESSION_START, 0L))
    }

    var screenOffStartTime by remember {
        mutableLongStateOf(prefs.getLong(KEY_SCREEN_OFF_START, 0L))
    }

    var xpCollected by remember {
        mutableStateOf(prefs.getBoolean(KEY_SLEEP_XP_COLLECTED, false))
    }

    var lastOfflineMinutes by remember {
        mutableIntStateOf(0)
    }

    var showOfflineResultPopup by remember {
        mutableStateOf(false)
    }

    val goalMinutes = sleepGoalHours * 60
    val progress = (lockedMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)
    val isCompleted = lockedMinutes >= goalMinutes

    fun saveSleepStateDirect(
        newSleepModeStarted: Boolean,
        newXpCollected: Boolean,
        newSessionStartTime: Long,
        newScreenOffStartTime: Long,
        newLockedMinutes: Int
    ) {
        prefs.edit()
            .putInt(KEY_SLEEP_DAY, today)
            .putBoolean(KEY_SLEEP_MODE_STARTED, newSleepModeStarted)
            .putBoolean(KEY_SLEEP_XP_COLLECTED, newXpCollected)
            .putLong(KEY_SLEEP_SESSION_START, newSessionStartTime)
            .putLong(KEY_SCREEN_OFF_START, newScreenOffStartTime)
            .putInt(KEY_SLEEP_LOCKED_MINUTES, newLockedMinutes)
            .apply()
    }

    fun handlePhoneActiveAgain(startTimeMillis: Long) {
        if (startTimeMillis <= 0L) {
            return
        }

        val now = System.currentTimeMillis()
        val offlineMinutes = max(0L, (now - startTimeMillis) / SLEEP_MINUTE_UNIT_MILLIS).toInt()

        lastOfflineMinutes = offlineMinutes

        val newLockedMinutes = if (offlineMinutes > 0) {
            lockedMinutes + offlineMinutes
        } else {
            lockedMinutes
        }

        lockedMinutes = newLockedMinutes
        screenOffStartTime = 0L

        saveSleepStateDirect(
            newSleepModeStarted = sleepModeStarted,
            newXpCollected = xpCollected,
            newSessionStartTime = sessionStartTime,
            newScreenOffStartTime = 0L,
            newLockedMinutes = newLockedMinutes
        )

        showOfflineResultPopup = true
    }

    DisposableEffect(
        sleepModeStarted,
        lockedMinutes,
        screenOffStartTime,
        xpCollected,
        sessionStartTime,
        goalMinutes
    ) {
        val sleepReceiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        if (sleepModeStarted && lockedMinutes < goalMinutes) {
                            val now = System.currentTimeMillis()

                            screenOffStartTime = now

                            saveSleepStateDirect(
                                newSleepModeStarted = sleepModeStarted,
                                newXpCollected = xpCollected,
                                newSessionStartTime = sessionStartTime,
                                newScreenOffStartTime = now,
                                newLockedMinutes = lockedMinutes
                            )
                        }
                    }

                    Intent.ACTION_SCREEN_ON,
                    Intent.ACTION_USER_PRESENT -> {
                        if (sleepModeStarted && screenOffStartTime > 0L) {
                            handlePhoneActiveAgain(screenOffStartTime)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                sleepReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(sleepReceiver, filter)
        }

        onDispose {
            runCatching {
                context.unregisterReceiver(sleepReceiver)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
                .padding(top = 6.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SleepTopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sleep Challenge",
                color = HabitTextDark,
                fontSize = 29.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Sleep well and help your buddy rest.",
                color = HabitTextDark,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            SleepHero()

            Spacer(modifier = Modifier.height(10.dp))

            HowItWorksCard()

            Spacer(modifier = Modifier.height(8.dp))

            TonightGoalCard(
                bedtimeGoal = bedtimeGoal,
                sleepGoalHours = sleepGoalHours,
                sleepModeStarted = sleepModeStarted,
                lockedMinutes = lockedMinutes,
                progress = progress,
                phoneCurrentlyLocked = screenOffStartTime > 0L
            )

            Spacer(modifier = Modifier.height(10.dp))

            SleepActionButton(
                sleepModeStarted = sleepModeStarted,
                isCompleted = isCompleted,
                onClick = {
                    when {
                        isCompleted -> {
                            xpCollected = true

                            saveSleepStateDirect(
                                newSleepModeStarted = sleepModeStarted,
                                newXpCollected = true,
                                newSessionStartTime = sessionStartTime,
                                newScreenOffStartTime = screenOffStartTime,
                                newLockedMinutes = lockedMinutes
                            )

                            onChallengeCompleted()
                        }

                        !sleepModeStarted -> {
                            val now = System.currentTimeMillis()

                            sleepModeStarted = true
                            sessionStartTime = now
                            screenOffStartTime = 0L
                            lastOfflineMinutes = 0

                            saveSleepStateDirect(
                                newSleepModeStarted = true,
                                newXpCollected = xpCollected,
                                newSessionStartTime = now,
                                newScreenOffStartTime = 0L,
                                newLockedMinutes = lockedMinutes
                            )
                        }

                        else -> {
                            // Sleep mode is already active.
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (sleepModeStarted && !isCompleted) {
                Text(
                    text = "Sleep Mode is active. Lock your phone before sleeping. When you return, HabitBloom shows how long you were offline.",
                    color = HabitTextGrey,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }

        if (showOfflineResultPopup) {
            OfflineSleepResultPopup(
                companionName = companionName,
                lastOfflineMinutes = lastOfflineMinutes,
                totalLockedMinutes = lockedMinutes,
                sleepGoalHours = sleepGoalHours,
                goalReached = lockedMinutes >= goalMinutes,
                onContinueSleeping = {
                    showOfflineResultPopup = false
                },
                onCollectXp = {
                    xpCollected = true
                    showOfflineResultPopup = false

                    saveSleepStateDirect(
                        newSleepModeStarted = sleepModeStarted,
                        newXpCollected = true,
                        newSessionStartTime = sessionStartTime,
                        newScreenOffStartTime = screenOffStartTime,
                        newLockedMinutes = lockedMinutes
                    )

                    onChallengeCompleted()
                }
            )
        }
    }
}

@Composable
private fun SleepTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(25.dp)
                .clip(CircleShape)
                .background(HabitCard)
                .border(1.dp, HabitBorder, CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = HabitTextDark,
                fontSize = 21.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            painter = painterResource(id = R.drawable.home_title),
            contentDescription = "Grow Daily",
            modifier = Modifier
                .align(Alignment.Center)
                .requiredSize(128.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun SleepHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(198.dp)
            .clip(RoundedCornerShape(10.dp))
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.sleep_background),
            contentDescription = "Sleep background",
            modifier = Modifier
                .requiredWidth(430.dp)
                .height(225.dp)
                .align(Alignment.Center)
                .offset(y = (-2).dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.sleep_plant_stage2),
            contentDescription = "Sleeping plant",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .requiredSize(148.dp)
                .offset(y = 8.dp),
            contentScale = ContentScale.Fit
        )

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 98.dp, y = (-18).dp),
            shape = RoundedCornerShape(23.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "I sleep\nwhen you\nsleep! 💜",
                color = HabitTextDark,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun HowItWorksCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(HabitLightGreen)
                    .border(1.dp, HabitBorder, RoundedCornerShape(17.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sleep_lock_green),
                    contentDescription = "Lock icon",
                    modifier = Modifier.requiredSize(55.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "How it works",
                    color = HabitTextDark,
                    fontSize = 17.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start Sleep Mode before bed.\nHabitBloom counts the time your phone stays locked and inactive.",
                    color = HabitTextDark,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Image(
                painter = painterResource(id = R.drawable.sleep_phone_icon),
                contentDescription = "Phone icon",
                modifier = Modifier.requiredSize(64.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun TonightGoalCard(
    bedtimeGoal: String,
    sleepGoalHours: Int,
    sleepModeStarted: Boolean,
    lockedMinutes: Int,
    progress: Float,
    phoneCurrentlyLocked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HabitBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sleep_goal_icon),
                    contentDescription = "Goal icon",
                    modifier = Modifier.requiredSize(48.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Tonight's Goal",
                    color = HabitTextDark,
                    fontSize = 21.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SleepGoalSmallCard(
                    icon = R.drawable.sleep_timer,
                    title = "Bedtime goal",
                    value = bedtimeGoal,
                    modifier = Modifier.weight(1f)
                )

                SleepGoalSmallCard(
                    icon = R.drawable.sleep_sleep_icon,
                    title = "Sleep goal",
                    value = "$sleepGoalHours hours",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "Put your phone away and stay off your phone during your sleep time.",
                color = HabitTextDark,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(13.dp))

            SleepStatusRow(
                icon = R.drawable.sleep_sleep_icon,
                title = "Sleep Mode",
                value = if (sleepModeStarted) "Running..." else "Not started"
            )

            Spacer(modifier = Modifier.height(10.dp))

            PhoneLockedRow(
                lockedMinutes = lockedMinutes,
                sleepGoalHours = sleepGoalHours,
                progress = progress,
                locked = phoneCurrentlyLocked
            )
        }
    }
}

@Composable
private fun SleepGoalSmallCard(
    icon: Int,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(74.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(13.dp)),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.requiredSize(44.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    color = HabitTextDark,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    color = HabitTextDark,
                    fontSize = 15.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun SleepStatusRow(
    icon: Int,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.requiredSize(42.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = HabitTextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = HabitTextGrey,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PhoneLockedRow(
    lockedMinutes: Int,
    sleepGoalHours: Int,
    progress: Float,
    locked: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = if (locked) {
                        R.drawable.sleep_lock_green
                    } else {
                        R.drawable.sleep_lock_blue
                    }
                ),
                contentDescription = "Phone locked",
                modifier = Modifier.requiredSize(42.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Phone locked",
                color = HabitTextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${formatLockedTime(lockedMinutes)} / ${sleepGoalHours}H",
                color = HabitTextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(HabitBorder.copy(alpha = 0.65f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(9.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(HabitGreen)
            )
        }
    }
}

@Composable
private fun SleepActionButton(
    sleepModeStarted: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = HabitGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sleep_sleep_icon),
                contentDescription = "Sleep button",
                modifier = Modifier.requiredSize(40.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = when {
                    isCompleted -> "Collect XP"
                    sleepModeStarted -> "Sleep Mode Active"
                    else -> "Start Sleep Mode"
                },
                color = HabitCard,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun OfflineSleepResultPopup(
    companionName: String,
    lastOfflineMinutes: Int,
    totalLockedMinutes: Int,
    sleepGoalHours: Int,
    goalReached: Boolean,
    onContinueSleeping: () -> Unit,
    onCollectXp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.26f))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = if (goalReached) HabitGreen else HabitBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = if (goalReached) {
                            R.drawable.sleep_sleep_icon
                        } else {
                            R.drawable.sleep_lock_blue
                        }
                    ),
                    contentDescription = "Sleep result",
                    modifier = Modifier.requiredSize(76.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (goalReached) {
                        "Sleep goal reached!"
                    } else {
                        "You were offline"
                    },
                    color = HabitTextDark,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Last offline time: ${formatLockedTime(lastOfflineMinutes)}",
                    color = HabitTextDark,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Total tracked sleep: ${formatLockedTime(totalLockedMinutes)} / ${sleepGoalHours}H",
                    color = HabitTextGrey,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (goalReached) {
                        "Great job! You stayed away from your phone long enough and helped $companionName rest."
                    } else {
                        "Not enough yet. Keep your phone locked longer to complete the sleep challenge."
                    },
                    color = HabitTextGrey,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = if (goalReached) "Collect XP" else "Continue Sleep Mode",
                    onClick = {
                        if (goalReached) {
                            onCollectXp()
                        } else {
                            onContinueSleeping()
                        }
                    }
                )
            }
        }
    }
}

private fun formatLockedTime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "${hours}H ${minutes.toString().padStart(2, '0')}m"
}

private fun currentDayOfYear(): Int {
    return Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
}

private const val SLEEP_PREFS = "habitbloom_sleep_prefs"
private const val KEY_SLEEP_DAY = "sleep_day"
private const val KEY_SLEEP_MODE_STARTED = "sleep_mode_started"
private const val KEY_SLEEP_LOCKED_MINUTES = "sleep_locked_minutes"
private const val KEY_SLEEP_SESSION_START = "sleep_session_start"
private const val KEY_SCREEN_OFF_START = "screen_off_start"
private const val KEY_SLEEP_XP_COLLECTED = "sleep_xp_collected"

/*
For real sleep tracking:
60_000L means 1 real minute = 1 sleep minute.

For fast emulator testing:
Change this to 1_000L.
Then 1 real second = 1 sleep minute.
*/
private const val SLEEP_MINUTE_UNIT_MILLIS = 60_000L