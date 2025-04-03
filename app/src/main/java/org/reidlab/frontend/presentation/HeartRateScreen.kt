package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite // Heart icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.scale

// --- Configuration ---
private const val MAX_HEART_RATE = 190 // Example Max Heart Rate (Adjust as needed)
private const val SIMULATION_DELAY_MS = 2000L // Update every 2 seconds

// --- Heart Rate Zone Colors ---
private val zone1Color = Color(0xFF89AFDC)
private val zone2Color = Color(0xFF64BC46)
private val zone3Color = Color(0xFFF9C018)
private val zone4Color = Color(0xFFF36B24)
private val zone5Color = Color(0xFFEC2529)
private val defaultColor = Color.DarkGray // Color for track or 0 HR

// --- Helper Function Assign Get Zone Color ---
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

// --- The Main UI Composable ---
@Composable
fun HeartRateScreen(isAnimationEnabled: Boolean) {
    // --- State for the current heart rate ---
    // In a real app, this would come from a ViewModel connected to sensors
    var currentHeartRate by remember { mutableStateOf(75) }

    // --- Simulate Heart Rate Changes (for demonstration) ---
    LaunchedEffect(Unit) { // Use Unit so it runs once on composition
        while (true) {
            delay(SIMULATION_DELAY_MS)
            // Simulate a plausible heart rate fluctuation
            currentHeartRate = (60..165).random() // Random value between 60 and 165
        }
    }

    // --- Calculate Progress and Color ---
    val activeZoneColor = getZoneColor(currentHeartRate, MAX_HEART_RATE)
    val zoneColor = if (isAnimationEnabled) activeZoneColor else Color.Gray
    val progress = if (isAnimationEnabled)
        (currentHeartRate.toFloat() / MAX_HEART_RATE).coerceIn(0f, 1f)
    else 0f

    // --- Wear Compose UI Structure ---
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // --- Heart Rate Progress Indicator ---
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize().scale(scaleX = -1f, scaleY = 1f),
                startAngle = 270f,
                endAngle = 270f,
                indicatorColor = zoneColor,
                trackColor = defaultColor.copy(alpha = 0.5f),
                strokeWidth = 8.dp
            )

            // --- Central Content (Text and Icon) ---
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
                    // --- Heart Rate Number ---
                    Text(
                        text = "$currentHeartRate",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp)) // Space between number and icon/bpm

                    // --- Icon and BPM Label ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Heart Rate Zone",
                            tint = zoneColor, // Icon color matches zone
                            modifier = Modifier.size(20.dp) // Adjust icon size
                        )
                        Text(
                            text = "bpm",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}