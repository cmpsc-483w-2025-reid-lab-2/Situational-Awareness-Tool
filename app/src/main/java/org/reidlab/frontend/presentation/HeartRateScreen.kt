package org.reidlab.frontend.presentation

import java.time.LocalDate
import java.time.Period
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text


// Zone colors based on:
// https://www8.garmin.com/manuals/webhelp/forerunner225/EN-US/GUID-DA94D501-8DA7-46A4-93D4-34504337272C.html
private val zone1Color = Color(0xFF89AFDC)
private val zone2Color = Color(0xFF64BC46)
private val zone3Color = Color(0xFFF9C018)
private val zone4Color = Color(0xFFF36B24)
private val zone5Color = Color(0xFFEC2529)
private val defaultColor = Color.DarkGray
private const val maxHeartRate = 190
private const val SIMULATION_DELAY_MS = 1000L
private const val DEFAULT_maxHeartRate = 190 // Default if age is unknown
private const val TAG = "HeartRateScreen"

fun getZoneColor(currentHr: Int, maxHr: Int): Color {
    if (currentHr <= 0 || maxHr <= 0) return defaultColor
    val percentage = (currentHr.toFloat() / maxHr * 100).toInt()
    return when {
        percentage <= 59 -> zone1Color
        percentage <= 69 -> zone2Color
        percentage <= 79 -> zone3Color
        percentage <= 89 -> zone4Color
        else -> zone5Color // 90+
    }
}

// Obtain current zone number for vibratio
fun getZoneNumber(currentHr: Int, maxHr: Int): Int {
    if (currentHr <= 0 || maxHr <= 0) return 0 // Zone 0 for invalid HR
    val percentage = (currentHr.toFloat() / maxHr * 100).toInt()
    return when {
        percentage <= 59 -> 1
        percentage <= 69 -> 2
        percentage <= 79 -> 3
        percentage <= 89 -> 4
        else -> 5
    }
}

// Millisecond precision formatter
private fun formatElapsedTimeMillis(totalMillis: Long): String {
    if (totalMillis < 0) return "00:00.0"
    val totalSeconds = totalMillis / 1000
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    val tenths = (totalMillis % 1000) / 100
    return "%02d:%02d.%d".format(minutes, seconds, tenths)
}

// Second precision formatter
private fun formatElapsedTimeSeconds(totalMillis: Long): String {
    if (totalMillis < 0) return "00:00"
    val totalSeconds = totalMillis / 1000
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
fun HeartRateScreen(
    measureDataViewModel: MeasureDataViewModel,
    isAnimationEnabled: Boolean,
    isTimerRunning: Boolean,
    elapsedTimeMillis: Long,
    showMilliseconds: Boolean,
    isHapticFeedbackEnabled: Boolean,
    isSimulationActive: Boolean,
    onStopTimer: () -> Unit,
    birthDate: LocalDate?
) {
    // Collect State from ViewModel
    val uiState by measureDataViewModel.uiState
    val availability by measureDataViewModel.availability
    val hrDouble by measureDataViewModel.hr // Real HR as Double
    val isEnabled by measureDataViewModel.enabled.collectAsState() // Real measurement active state

    // Simulation State and Logic
    var currentHeartRate by remember { mutableStateOf<Int?>(null) } // Simulated HR (Int?)
    val allowedHeartRate = listOf(65, 80, 95, 110, 120, 140, 160, 180)
    // Relaunch simulation effect whenever isSimulationActive changes
    // Test for haptic feedback i.e., Zone 4 & 5
    // val allowedHeartRate = listOf(120, 160, 200)

    val age = remember(birthDate) { // Recalculate only if birthDate changes
        birthDate?.let { Period.between(it, LocalDate.now()).years }
    }
    val maxHeartRate = remember(age) { // Recalculate only if age changes
        // Use 220 - age formula, provide default if age is unknown
        if (age != null && age > 0) 220 - age else DEFAULT_maxHeartRate
    }

    // Relaunch the effect whenever isSimulationActive changes
    LaunchedEffect(isSimulationActive) {
        if (isSimulationActive) {
            var currentIndex = 0
            var isIncreasing = true
            currentHeartRate = allowedHeartRate[currentIndex] // Start simulation
            while (isActive) { // Use isActive from coroutine scope
                delay(SIMULATION_DELAY_MS)
                // Check simulation is still desired *after* delay
                if (!isSimulationActive) break // Exit if simulation turned off

                if (isIncreasing) {
                    if (currentIndex < allowedHeartRate.lastIndex) currentIndex++ else { isIncreasing = false; currentIndex-- }
                } else { // Decreasing
                    if (currentIndex > 0) currentIndex-- else { isIncreasing = true; currentIndex++ }
                }

                if (currentIndex in allowedHeartRate.indices) {
                    currentHeartRate = allowedHeartRate[currentIndex]
                } else { // Safety reset
                    currentIndex = 0; isIncreasing = true; currentHeartRate = allowedHeartRate[currentIndex]
                }
            }
        } else {
            // If simulation stops, clear the simulated value
            currentHeartRate = null
        }
    }

    // Control Real Measurement based on Simulation State
    LaunchedEffect(isEnabled, isSimulationActive) {
        when {
            // If simulation is ON, ensure real measurement is OFF
            isSimulationActive && isEnabled -> {
                measureDataViewModel.toggleEnabled() // Turn off real measurement
            }
            // If simulation is OFF, ensure real measurement is ON
            !isSimulationActive && !isEnabled -> {
                measureDataViewModel.toggleEnabled() // Turn on real measurement
            }
        }
    }

    val context = LocalContext.current
    val vibrator = remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    }
    var previousZone by remember { mutableStateOf(0) }

    // Determine Active HR Data Source
    val activeHrInt: Int?
    val isDataActuallyAvailable: Boolean // Tracks if we have *any* valid HR (simulated or real)

    if (isSimulationActive) {
        activeHrInt = currentHeartRate // Use simulated Int?
        isDataActuallyAvailable = activeHrInt != null
    } else {
        // Use real data only if available and measurement is enabled
        if (availability == DataTypeAvailability.AVAILABLE && isEnabled) {
            activeHrInt = hrDouble.toInt()
            isDataActuallyAvailable = true
        } else {
            activeHrInt = null // No real data available/enabled
            isDataActuallyAvailable = false
        }
    }

    // Calculations based on Active HR
    // Use 0 for calculations if activeHrInt is null
    val calcHr = activeHrInt ?: 0
    val currentZone = getZoneNumber(calcHr, maxHeartRate)
    val zoneColor = if (isDataActuallyAvailable) getZoneColor(calcHr, maxHeartRate) else defaultColor
    val progress = if (isDataActuallyAvailable) {
        (calcHr.toFloat() / maxHeartRate).coerceIn(0f, 1f)
    } else 0f

    // Haptics (React to currentZone derived from active HR)
    LaunchedEffect(currentZone, isHapticFeedbackEnabled) { // Keyed on zone and setting
        if (isHapticFeedbackEnabled && vibrator?.hasVibrator() == true) {
            // Trigger vibration based ONLY on zone transitions/state, regardless of source
            if (currentZone == 4 && previousZone < 4) { // Entered Zone 4
                while (isActive && getZoneNumber(activeHrInt ?: 0, maxHeartRate) == 4) { // Check zone directly
                    val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1)
                    vibrator.vibrate(effect)
                    delay(950L)
                }
            }
        }
        previousZone = currentZone // Update previous zone *after* checking entry condition
    }

    LaunchedEffect(currentZone, isHapticFeedbackEnabled) { // Keyed on zone and setting
        if (isHapticFeedbackEnabled && vibrator?.hasVibrator() == true) {
            if (currentZone == 5) { // Entered or staying in Zone 5
                while (isActive && getZoneNumber(activeHrInt ?: 0, maxHeartRate) == 5) { // Check zone directly
                    val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1)
                    vibrator.vibrate(effect)
                    delay(450L)
                }
            }
        }
        // previousZone updated in the other effect
    }

    // Animation
    val infiniteTransition = rememberInfiniteTransition(label = "Heartbeat")

    // Heart scale animation depends only on isAnimationEnabled
    val heartScaleTarget = if (isAnimationEnabled) 1.2f else 1f
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimationEnabled) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartbeatScale"
    )
    // End Animation

    Scaffold {
        // Conditional UI based on Simulation OR Real Data State
        if (isSimulationActive) {
            // Render UI using Simulation Data (activeHrInt, zoneColor, progress derived above)
            HeartRateDisplayContent(
                hrText = activeHrInt?.toString() ?: "--",
                isDataAvailable = isDataActuallyAvailable,
                zoneColor = zoneColor,
                progress = progress,
                heartScale = heartScale,
                isTimerRunning = isTimerRunning,
                elapsedTimeMillis = elapsedTimeMillis,
                showMilliseconds = showMilliseconds,
                onStopTimer = onStopTimer
            )
        } else {
            // Render UI based on Real Data State (UiState, availability, etc.)
            when (uiState) {
                UiState.Startup -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                UiState.NotSupported -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Heart Rate Sensor Not Available or Not Supported.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                UiState.Supported -> {
                    // Render UI using Real Data (activeHrInt, zoneColor, progress derived above)
                    HeartRateDisplayContent(
                        // Use activeHrInt which reflects real data availability when !isSimulationActive
                        hrText = activeHrInt?.toString() ?: "--",
                        isDataAvailable = isDataActuallyAvailable,
                        zoneColor = zoneColor,
                        progress = progress,
                        heartScale = heartScale,
                        isTimerRunning = isTimerRunning,
                        elapsedTimeMillis = elapsedTimeMillis,
                        showMilliseconds = showMilliseconds,
                        onStopTimer = onStopTimer
                    )
                }
            }
        }
    }
}


// Extracted common UI content Composable
@Composable
private fun HeartRateDisplayContent(
    hrText: String,
    isDataAvailable: Boolean,
    zoneColor: Color,
    progress: Float,
    heartScale: Float,
    isTimerRunning: Boolean,
    elapsedTimeMillis: Long,
    showMilliseconds: Boolean,
    onStopTimer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clickable(
                onClick = { if (isTimerRunning) onStopTimer() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize().scale(scaleX = -1f, scaleY = 1f),
            startAngle = 270f,
            endAngle = 270f,
            indicatorColor = zoneColor,
            trackColor = defaultColor.copy(alpha = 0.5f),
            strokeWidth = 8.dp
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Current",
                color = Color.White,
                fontSize = 14.sp,
                style = TextStyle(letterSpacing = 0.1.em)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hrText, // Use passed hrText
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Heart Rate Zone",
                        tint = zoneColor, // Use passed zoneColor
                        modifier = Modifier
                            .size(20.dp)
                            .scale(heartScale) // Use passed heartScale
                    )
                    Text(
                        text = "bpm",
                        // Hide bpm text if data (sim or real) is unavailable
                        color = if (isDataAvailable) Color.White else Color.Transparent,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Display the timer text conditionally
        if (isTimerRunning) {
            Text(
                text = if (showMilliseconds) {
                    formatElapsedTimeMillis(elapsedTimeMillis)
                } else {
                    formatElapsedTimeSeconds(elapsedTimeMillis)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}