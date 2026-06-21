package com.life.habitbloom.exercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.life.habitbloom.R
import com.life.habitbloom.components.PrimaryButton
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.ui.theme.HabitBorder
import com.life.habitbloom.ui.theme.HabitCard
import com.life.habitbloom.ui.theme.HabitCream
import com.life.habitbloom.ui.theme.HabitGreen
import com.life.habitbloom.ui.theme.HabitLightGreen
import com.life.habitbloom.ui.theme.HabitTextDark
import com.life.habitbloom.ui.theme.HabitTextGrey
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun MovementChallengePage(
    companionName: String,
    userGender: GenderType,
    onBackClick: () -> Unit,
    onChallengeCompleted: () -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext

    val prefs = remember {
        appContext.getSharedPreferences(MOVEMENT_PREFS, Context.MODE_PRIVATE)
    }

    val today = remember { currentDayOfYear() }

    LaunchedEffect(Unit) {
        val savedDay = prefs.getInt(KEY_MOVEMENT_DAY, -1)

        if (savedDay != today) {
            prefs.edit()
                .putInt(KEY_MOVEMENT_DAY, today)
                .putInt(KEY_MOVEMENT_STEPS, 0)
                .putInt(KEY_STEP_COUNTER_BASELINE, -1)
                .putLong(KEY_MOVEMENT_ELAPSED, 0L)
                .putBoolean(KEY_MOVEMENT_COMPLETED, false)
                .apply()
        }
    }

    var hasStepPermission by remember {
        mutableStateOf(hasActivityRecognitionPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStepPermission = granted
    }

    var currentSteps by remember {
        mutableIntStateOf(prefs.getInt(KEY_MOVEMENT_STEPS, 0))
    }

    var stepCounterBaseline by remember {
        mutableIntStateOf(prefs.getInt(KEY_STEP_COUNTER_BASELINE, -1))
    }

    var elapsedMillis by remember {
        mutableLongStateOf(prefs.getLong(KEY_MOVEMENT_ELAPSED, 0L))
    }

    var lastTimerTickMillis by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var isPaused by remember {
        mutableStateOf(false)
    }

    var challengeCompleted by remember {
        mutableStateOf(prefs.getBoolean(KEY_MOVEMENT_COMPLETED, false))
    }

    var bestSteps by remember {
        mutableIntStateOf(prefs.getInt(KEY_MOVEMENT_BEST_STEPS, 0))
    }

    var oldBestForPopup by remember { mutableIntStateOf(0) }
    var newBestForPopup by remember { mutableIntStateOf(0) }
    var showCompletionPopup by remember { mutableStateOf(false) }
    var sensorStatus by remember { mutableStateOf("Preparing step tracker...") }
    var frameIndex by remember { mutableIntStateOf(0) }

    val remainingMillis = max(0L, WALK_DURATION_MILLIS - elapsedMillis)

    val remainingTimerProgress =
        (remainingMillis.toFloat() / WALK_DURATION_MILLIS.toFloat()).coerceIn(0f, 1f)

    val stepProgress =
        (currentSteps.toFloat() / STEP_GOAL.toFloat()).coerceIn(0f, 1f)

    val isWalking = !isPaused && !challengeCompleted

    fun saveMovementState(
        steps: Int = currentSteps,
        baseline: Int = stepCounterBaseline,
        elapsed: Long = elapsedMillis,
        completed: Boolean = challengeCompleted,
        best: Int = bestSteps
    ) {
        prefs.edit()
            .putInt(KEY_MOVEMENT_DAY, today)
            .putInt(KEY_MOVEMENT_STEPS, steps)
            .putInt(KEY_STEP_COUNTER_BASELINE, baseline)
            .putLong(KEY_MOVEMENT_ELAPSED, elapsed)
            .putBoolean(KEY_MOVEMENT_COMPLETED, completed)
            .putInt(KEY_MOVEMENT_BEST_STEPS, best)
            .apply()
    }

    LaunchedEffect(Unit) {
        if (!hasStepPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    LaunchedEffect(isWalking) {
        while (true) {
            delay(270)

            if (isWalking) {
                frameIndex = (frameIndex + 1) % 3
            }
        }
    }

    LaunchedEffect(isPaused, challengeCompleted) {
        lastTimerTickMillis = System.currentTimeMillis()

        while (!isPaused && !challengeCompleted) {
            delay(250)

            val now = System.currentTimeMillis()
            val difference = (now - lastTimerTickMillis).coerceAtLeast(0L)
            lastTimerTickMillis = now

            if (difference > 0L && elapsedMillis < WALK_DURATION_MILLIS) {
                val newElapsed = (elapsedMillis + difference).coerceAtMost(WALK_DURATION_MILLIS)
                elapsedMillis = newElapsed
                saveMovementState(elapsed = newElapsed)
            }
        }
    }

    LaunchedEffect(currentSteps) {
        if (currentSteps >= STEP_GOAL && !challengeCompleted) {
            val oldBest = bestSteps
            val newBest = max(bestSteps, currentSteps)

            oldBestForPopup = oldBest
            newBestForPopup = newBest

            bestSteps = newBest
            challengeCompleted = true
            showCompletionPopup = true

            saveMovementState(
                steps = currentSteps,
                completed = true,
                best = newBest
            )
        }
    }

    DisposableEffect(hasStepPermission, isPaused, challengeCompleted) {
        if (!hasStepPermission) {
            sensorStatus = "Step permission needed"
            onDispose { }
        } else {
            val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

            val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val selectedSensor = stepCounterSensor ?: stepDetectorSensor ?: accelerometerSensor

            if (selectedSensor == null) {
                sensorStatus = "No motion sensor found"
                onDispose { }
            } else {
                sensorStatus = when (selectedSensor.type) {
                    Sensor.TYPE_STEP_COUNTER -> "Tracking real steps"
                    Sensor.TYPE_STEP_DETECTOR -> "Tracking real steps"
                    Sensor.TYPE_ACCELEROMETER -> "Emulator motion fallback"
                    else -> "Tracking movement"
                }

                var lastAccelerationMagnitude = 0f
                var lastStepTime = 0L

                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event == null) return

                        when (event.sensor.type) {
                            Sensor.TYPE_STEP_COUNTER -> {
                                val rawSteps = event.values.firstOrNull()?.toInt() ?: return

                                if (stepCounterBaseline < 0) {
                                    stepCounterBaseline = rawSteps
                                    saveMovementState(baseline = rawSteps)
                                }

                                if (isPaused || challengeCompleted) {
                                    val correctedBaseline = rawSteps - currentSteps
                                    stepCounterBaseline = correctedBaseline
                                    saveMovementState(baseline = correctedBaseline)
                                    return
                                }

                                val calculatedSteps =
                                    (rawSteps - stepCounterBaseline).coerceAtLeast(currentSteps)

                                currentSteps = calculatedSteps
                                saveMovementState(steps = calculatedSteps)
                            }

                            Sensor.TYPE_STEP_DETECTOR -> {
                                if (!isPaused && !challengeCompleted) {
                                    val newSteps = currentSteps + 1
                                    currentSteps = newSteps
                                    saveMovementState(steps = newSteps)
                                }
                            }

                            Sensor.TYPE_ACCELEROMETER -> {
                                if (isPaused || challengeCompleted) {
                                    return
                                }

                                val x = event.values.getOrNull(0) ?: 0f
                                val y = event.values.getOrNull(1) ?: 0f
                                val z = event.values.getOrNull(2) ?: 0f

                                val magnitude = sqrt(x * x + y * y + z * z)
                                val movementChange = abs(magnitude - lastAccelerationMagnitude)
                                val now = System.currentTimeMillis()

                                if (movementChange > ACCELEROMETER_STEP_THRESHOLD &&
                                    now - lastStepTime > ACCELEROMETER_STEP_DELAY
                                ) {
                                    lastStepTime = now

                                    val newSteps = currentSteps + 1
                                    currentSteps = newSteps
                                    saveMovementState(steps = newSteps)
                                }

                                lastAccelerationMagnitude = magnitude
                            }
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        // No action needed.
                    }
                }

                sensorManager.registerListener(
                    listener,
                    selectedSensor,
                    SensorManager.SENSOR_DELAY_GAME
                )

                onDispose {
                    sensorManager.unregisterListener(listener)
                }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MovementHeroSection(
                userGender = userGender,
                frameIndex = frameIndex,
                remainingMillis = remainingMillis,
                remainingTimerProgress = remainingTimerProgress,
                currentSteps = currentSteps,
                isWalking = isWalking
            )

            MovementBottomPanel(
                currentSteps = currentSteps,
                stepProgress = stepProgress,
                sensorStatus = sensorStatus,
                isPaused = isPaused,
                hasStepPermission = hasStepPermission,
                onBackClick = onBackClick,
                onPauseToggle = {
                    isPaused = !isPaused
                    lastTimerTickMillis = System.currentTimeMillis()
                    saveMovementState()
                },
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    } else {
                        hasStepPermission = true
                    }
                }
            )
        }

        if (showCompletionPopup) {
            MovementCompletePopup(
                companionName = companionName,
                currentSteps = currentSteps,
                oldBest = oldBestForPopup,
                newBest = newBestForPopup,
                onCollectXp = {
                    showCompletionPopup = false
                    saveMovementState(completed = true)
                    onChallengeCompleted()
                }
            )
        }
    }
}

@Composable
private fun MovementHeroSection(
    userGender: GenderType,
    frameIndex: Int,
    remainingMillis: Long,
    remainingTimerProgress: Float,
    currentSteps: Int,
    isWalking: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(505.dp)
    ) {
        MovementParkBackground()

        Image(
            painter = painterResource(id = R.drawable.home_title),
            contentDescription = "Grow Daily",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .requiredSize(116.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 58.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Walk for 10 minutes",
                color = HabitTextDark,
                fontSize = 25.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Take a short walk and keep your streak going.",
                color = HabitGreen,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        MovementTimerCircle(
            remainingMillis = remainingMillis,
            remainingTimerProgress = remainingTimerProgress,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp)
        )

        FootprintsTrail(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 285.dp)
        )

        WalkingCharacters(
            userGender = userGender,
            frameIndex = frameIndex,
            isWalking = isWalking,
            currentSteps = currentSteps,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-45).dp)
        )
    }
}

@Composable
private fun MovementParkBackground() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFBCEBFF),
                    Color(0xFFE9F8FF),
                    Color(0xFFF7F4CB),
                    Color(0xFFE9F6B2)
                )
            )
        )

        drawCircle(
            color = Color(0xFFFFF1A6),
            radius = 36.dp.toPx(),
            center = Offset(width * 0.18f, height * 0.14f)
        )

        drawOval(
            color = Color(0xFFA7DF73),
            topLeft = Offset(-width * 0.36f, height * 0.23f),
            size = Size(width * 0.72f, height * 0.58f)
        )

        drawOval(
            color = Color(0xFF8ED066),
            topLeft = Offset(-width * 0.22f, height * 0.31f),
            size = Size(width * 0.50f, height * 0.48f)
        )

        drawOval(
            color = Color(0xFFA0DA72),
            topLeft = Offset(width * 0.64f, height * 0.22f),
            size = Size(width * 0.72f, height * 0.58f)
        )

        drawOval(
            color = Color(0xFF8ACB62),
            topLeft = Offset(width * 0.78f, height * 0.31f),
            size = Size(width * 0.50f, height * 0.48f)
        )

        val path = Path().apply {
            moveTo(width * 0.46f, height * 0.25f)

            cubicTo(
                width * 0.36f,
                height * 0.44f,
                width * 0.25f,
                height * 0.68f,
                width * 0.10f,
                height
            )

            lineTo(width * 0.90f, height)

            cubicTo(
                width * 0.75f,
                height * 0.68f,
                width * 0.64f,
                height * 0.44f,
                width * 0.54f,
                height * 0.25f
            )

            close()
        }

        drawPath(
            path = path,
            color = Color(0xFFF2D48C)
        )

        drawPath(
            path = path,
            color = Color(0xFFD3A662),
            style = Stroke(width = 2.dp.toPx())
        )

        repeat(22) { index ->
            val leftSide = index % 2 == 0

            val flowerX = if (leftSide) {
                width * (0.12f + ((index % 5) * 0.045f))
            } else {
                width * (0.73f + ((index % 5) * 0.045f))
            }

            val flowerY = height * (0.43f + ((index % 7) * 0.055f))

            drawCircle(
                color = Color.White,
                radius = 3.2.dp.toPx(),
                center = Offset(flowerX, flowerY)
            )

            drawCircle(
                color = Color(0xFFFFD46B),
                radius = 1.3.dp.toPx(),
                center = Offset(flowerX, flowerY)
            )
        }

        drawRect(
            color = Color(0xFF7D5437),
            topLeft = Offset(width * 0.18f, height * 0.51f),
            size = Size(5.dp.toPx(), 68.dp.toPx())
        )

        drawCircle(
            color = Color(0xFFFFF7D6),
            radius = 12.dp.toPx(),
            center = Offset(width * 0.19f, height * 0.48f)
        )

        drawRect(
            color = Color(0xFF7D5437),
            topLeft = Offset(width * 0.81f, height * 0.51f),
            size = Size(5.dp.toPx(), 68.dp.toPx())
        )

        drawCircle(
            color = Color(0xFFFFF7D6),
            radius = 12.dp.toPx(),
            center = Offset(width * 0.82f, height * 0.48f)
        )
    }
}

@Composable
private fun MovementTimerCircle(
    remainingMillis: Long,
    remainingTimerProgress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(126.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2f
            )

            drawCircle(
                color = HabitBorder.copy(alpha = 0.65f),
                radius = size.minDimension / 2f,
                style = Stroke(width = 8.dp.toPx())
            )

            drawArc(
                color = HabitGreen,
                startAngle = -90f,
                sweepAngle = 360f * remainingTimerProgress,
                useCenter = false,
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TIME REMAINING",
                color = HabitGreen,
                fontSize = 7.sp,
                lineHeight = 8.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatMillisAsTimer(remainingMillis),
                color = HabitTextDark,
                fontSize = 28.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Image(
                painter = painterResource(id = R.drawable.page3_icon1),
                contentDescription = "Movement icon",
                modifier = Modifier.requiredSize(22.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun FootprintsTrail(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(5) { index ->
            Image(
                painter = painterResource(id = R.drawable.movement_footprint),
                contentDescription = "Footprints",
                modifier = Modifier
                    .requiredSize(31.dp)
                    .offset(
                        x = if (index % 2 == 0) (-12).dp else 12.dp,
                        y = (index * -5).dp
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun WalkingCharacters(
    userGender: GenderType,
    frameIndex: Int,
    isWalking: Boolean,
    currentSteps: Int,
    modifier: Modifier = Modifier
) {
    val personImage = when (userGender) {
        GenderType.BOY -> when (frameIndex) {
            0 -> R.drawable.movement_boy_1
            1 -> R.drawable.movement_boy_2
            else -> R.drawable.movement_boy_3
        }

        GenderType.GIRL,
        GenderType.PREFER_NOT_TO_SAY -> when (frameIndex) {
            0 -> R.drawable.movement_girl_1
            1 -> R.drawable.movement_girl_2
            else -> R.drawable.movement_girl_3
        }
    }

    val plantImage = if (frameIndex % 2 == 0) {
        R.drawable.movement_plant2_1
    } else {
        R.drawable.movement_plant2_2
    }

    val personStepOffset by animateDpAsState(
        targetValue = if (isWalking && frameIndex == 1) (-6).dp else 0.dp,
        animationSpec = tween(durationMillis = 210),
        label = "personStepOffset"
    )

    val personSideOffset by animateDpAsState(
        targetValue = if (isWalking && frameIndex == 2) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 210),
        label = "personSideOffset"
    )

    val plantHopOffset by animateDpAsState(
        targetValue = if (isWalking && frameIndex % 2 == 1) (-9).dp else 0.dp,
        animationSpec = tween(durationMillis = 210),
        label = "plantHopOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(238.dp)
    ) {
        Image(
            painter = painterResource(id = personImage),
            contentDescription = "Walking user",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = (-58).dp + personSideOffset, y = personStepOffset)
                .requiredSize(184.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = plantImage),
            contentDescription = "Walking buddy",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 58.dp, y = 14.dp + plantHopOffset)
                .requiredSize(98.dp),
            contentScale = ContentScale.Fit
        )

        Card(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp)
                .offset(y = (-34).dp),
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(containerColor = HabitCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "$currentSteps\nsteps",
                color = HabitTextDark,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun MovementBottomPanel(
    currentSteps: Int,
    stepProgress: Float,
    sensorStatus: String,
    isPaused: Boolean,
    hasStepPermission: Boolean,
    onBackClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .offset(y = (-8).dp),
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
        ),
        colors = CardDefaults.cardColors(containerColor = HabitCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MovementSmallButton(
                    text = "Back",
                    iconText = "←",
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    onClick = onBackClick
                )

                MovementSmallButton(
                    text = if (isPaused) "Resume" else "Pause",
                    iconText = if (isPaused) "▶" else "Ⅱ",
                    isPrimary = true,
                    modifier = Modifier.weight(1f),
                    onClick = onPauseToggle
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                XpRewardCard(
                    modifier = Modifier.weight(1f)
                )

                StepsCard(
                    currentSteps = currentSteps,
                    stepProgress = stepProgress,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MovementTipCard(
                sensorStatus = sensorStatus,
                hasStepPermission = hasStepPermission,
                onRequestPermission = onRequestPermission
            )
        }
    }
}

@Composable
private fun MovementSmallButton(
    text: String,
    iconText: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(55.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) Color(0xFF2F9CED) else HabitCream
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = if (isPrimary) Color.Transparent else HabitBorder,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = iconText,
                color = if (isPrimary) Color.White else Color(0xFFAD6A15),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = if (isPrimary) Color.White else HabitGreen,
                fontSize = 17.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun XpRewardCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(88.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_level),
                    contentDescription = "XP",
                    modifier = Modifier.requiredSize(50.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "XP",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "+ ${XP_REWARD}XP",
                    color = HabitGreen,
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "for completing\nthis challenge",
                    color = Color(0xFF3F64E8),
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StepsCard(
    currentSteps: Int,
    stepProgress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(88.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(HabitLightGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.movement_footprint),
                    contentDescription = "Steps",
                    modifier = Modifier.requiredSize(40.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Steps",
                    color = HabitTextDark,
                    fontSize = 15.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                MovementProgressBar(
                    progress = stepProgress,
                    height = 8
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$currentSteps / $STEP_GOAL",
                    color = HabitTextGrey,
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MovementTipCard(
    sensorStatus: String,
    hasStepPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (hasStepPermission) 80.dp else 104.dp)
            .border(1.dp, HabitBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HabitCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(HabitLightGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.movement_leave),
                    contentDescription = "Leaf",
                    modifier = Modifier.requiredSize(37.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "A short walk can boost energy and replace sitting time.",
                    color = HabitTextDark,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = sensorStatus,
                    color = HabitTextGrey,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!hasStepPermission) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tap here to enable step tracking",
                        color = HabitGreen,
                        fontSize = 11.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clickable { onRequestPermission() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MovementProgressBar(
    progress: Float,
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
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(HabitGreen)
        )
    }
}

@Composable
private fun MovementCompletePopup(
    companionName: String,
    currentSteps: Int,
    oldBest: Int,
    newBest: Int,
    onCollectXp: () -> Unit
) {
    val improvedBest = newBest > oldBest

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.28f))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, HabitGreen, RoundedCornerShape(24.dp)),
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
                    painter = painterResource(id = R.drawable.page3_icon1),
                    contentDescription = "Movement complete",
                    modifier = Modifier.requiredSize(70.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Movement complete!",
                    color = HabitTextDark,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Great job! You reached your step goal and helped $companionName stay active.",
                    color = HabitTextGrey,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Steps today: $currentSteps / $STEP_GOAL",
                    color = HabitTextDark,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                if (improvedBest) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "New best! Old best: $oldBest steps → New best: $newBest steps",
                        color = HabitGreen,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Collect XP",
                    onClick = onCollectXp
                )
            }
        }
    }
}

private fun hasActivityRecognitionPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun formatMillisAsTimer(millis: Long): String {
    val totalSeconds = millis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

private fun currentDayOfYear(): Int {
    return Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
}

private const val MOVEMENT_PREFS = "habitbloom_movement_prefs"
private const val KEY_MOVEMENT_DAY = "movement_day"
private const val KEY_MOVEMENT_STEPS = "movement_steps"
private const val KEY_STEP_COUNTER_BASELINE = "movement_step_counter_baseline"
private const val KEY_MOVEMENT_ELAPSED = "movement_elapsed"
private const val KEY_MOVEMENT_COMPLETED = "movement_completed"
private const val KEY_MOVEMENT_BEST_STEPS = "movement_best_steps"

private const val WALK_DURATION_MILLIS = 10 * 60 * 1000L
private const val STEP_GOAL = 5000
private const val XP_REWARD = 10

private const val ACCELEROMETER_STEP_THRESHOLD = 0.65f
private const val ACCELEROMETER_STEP_DELAY = 260L