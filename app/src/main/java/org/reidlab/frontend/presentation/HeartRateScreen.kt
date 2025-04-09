package org.reidlab.frontend.presentation

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.platform.LocalContext

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive // Import isActive

private const val MAX_HEART_RATE = 190
private const val SIMULATION_DELAY_MS = 1000L

// Zone colors based on:
// https://www8.garmin.com/manuals/webhelp/forerunner225/EN-US/GUID-DA94D501-8DA7-46A4-93D4-34504337272C.html
private val zone1Color = Color(0xFF89AFDC)
private val zone2Color = Color(0xFF64BC46)
private val zone3Color = Color(0xFFF9C018)
private val zone4Color = Color(0xFFF36B24)
private val zone5Color = Color(0xFFEC2529)
private val defaultColor = Color.DarkGray

fun getZoneColor(currentHr: Int, maxHr: Int): Color {
    // Handle case where HR is 0 or less (e.g., when simulation is off)
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

// Obtain current zone number for vibration
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
    isAnimationEnabled: Boolean,
    isTimerRunning: Boolean,
    elapsedTimeMillis: Long,
    showMilliseconds: Boolean,
    isHapticFeedbackEnabled: Boolean,
    isSimulationActive: Boolean, // New parameter for the toggle state
    onStopTimer: () -> Unit
) {
    // Use nullable Int? to represent the absence of a value when simulation is off
    var currentHeartRate by remember { mutableStateOf<Int?>(null) }
    val allowedHeartRate = listOf(160, 180)

    val context = LocalContext.current
    val vibrator = remember {
        // Use context.getSystemService with type casting
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator? // Use nullable type for safety
    }

    // State to track previous zone for detecting entry
    var previousZone by remember { mutableStateOf(0) }

    // Relaunch the effect whenever isSimulationActive changes
    LaunchedEffect(isSimulationActive) {
        if (isSimulationActive) {
            // State for cycling within the effect scope
            // Start at the first element index
            var currentIndex = 0
            // Start by moving towards the larger end
            var isIncreasing = true
            // Set the initial heart rate when simulation starts/restarts
            // Always start from the beginning of the list when activated
            currentHeartRate = allowedHeartRate[currentIndex]
            // Loop while the effect is active and simulation should be running
            while (isActive) { // Use isActive from coroutine scope
                delay(SIMULATION_DELAY_MS) // Wait for the delay first

                // Ensure simulation is still active after the delay
                if (isSimulationActive) {
                    // Calculate the next index
                    if (isIncreasing) {
                        if (currentIndex < allowedHeartRate.lastIndex) {
                            // Move to the next item if not at the end
                            currentIndex++
                        } else {
                            // Reached the end, switch direction and move back
                            isIncreasing = false
                            currentIndex--
                        }
                    } else { // Decreasing
                        if (currentIndex > 0) {
                            // Move to the previous item if not at the beginning
                            currentIndex--
                        } else {
                            // Reached the beginning, switch direction and move forward
                            isIncreasing = true
                            currentIndex++
                        }
                    }

                    // Update the heart rate state with the value at the new index
                    // Add a safety check just in case (though logic should prevent out of bounds)
                    if (currentIndex in allowedHeartRate.indices) {
                        currentHeartRate = allowedHeartRate[currentIndex]
                    } else {
                        // Should not happen with current logic, but as a fallback reset
                        currentIndex = 0
                        isIncreasing = true
                        currentHeartRate = allowedHeartRate[currentIndex]
                        println("Warning: Heart rate index out of bounds, resetting.") // Optional log
                    }

                } else {
                    // Break the loop if simulation turned off during the delay
                    break
                }
            }
        } else {
            // If simulation is not active (or becomes inactive), set heart rate to null
            currentHeartRate = null
        }
    }

    // Calculate current zone number (0 if HR is null)
    val currentZone = getZoneNumber(currentHeartRate ?: 0, MAX_HEART_RATE)

    // LaunchedEffect for repeating vibration in Zone 4
    LaunchedEffect(currentZone, isHapticFeedbackEnabled) {
        // Trigger only when moving from a lower zone into zone 4
        if (currentZone == 4 && previousZone < 4) {
            if (isHapticFeedbackEnabled && vibrator?.hasVibrator() == true) { // Check for null and if vibrator exists
                while (isActive) {
                    // Create a short vibration pattern
                    val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1)
                    vibrator.vibrate(effect)
                    // Wait for a defined interval before the next warning vibration
                    delay(950L)
                }
            }
        }
        // Update previous zone *after* the check for next comparison
        previousZone = currentZone
    }

    // LaunchedEffect for repeating vibration while in Zone 5
    LaunchedEffect(currentZone, isHapticFeedbackEnabled) {
        // Start repeating when zone becomes 5, stop when it's not 5 anymore
        if (currentZone == 5) {
            if (isHapticFeedbackEnabled && vibrator?.hasVibrator() == true) {
                while (isActive) {
                    // Create a short vibration pattern (e.g., two short buzzes)
                    val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1)
                    vibrator.vibrate(effect)
                    // Wait for a defined interval before the next warning vibration
                    delay(450L)
                }
            }
        }
        // When currentZone changes from 5, vibration stops
    }

    // Use currentHeartRate ?: 0 to safely handle the null case for calculations
    // getZoneColor already handles <= 0 returning defaultColor
    val activeZoneColor = getZoneColor(currentHeartRate ?: 0, MAX_HEART_RATE)

    // Zone color and progress depend on animation enabled AND a valid HR
    val displayActive = isAnimationEnabled && currentHeartRate != null
    val zoneColor = if (displayActive) activeZoneColor else defaultColor
    val progress = if (displayActive) {
        ((currentHeartRate ?: 0).toFloat() / MAX_HEART_RATE).coerceIn(0f, 1f)
    } else 0f


    val infiniteTransition = rememberInfiniteTransition(label = "Heartbeat")

    // Heart scale animation depends only on isAnimationEnabled
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimationEnabled) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartbeatScale"
    )

    Scaffold {
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
                        // Display "--" if currentHeartRate is null, otherwise the number
                        text = currentHeartRate?.toString() ?: "--",
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
                            // Icon color reflects active state
                            tint = zoneColor,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(heartScale) // Scale animation runs independently based on isAnimationEnabled
                        )
                        Text(
                            text = "bpm",
                            // Hide bpm text if HR is null
                            color = if (currentHeartRate != null) Color.White else Color.Transparent,
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
}