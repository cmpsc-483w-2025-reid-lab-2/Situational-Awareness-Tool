package org.reidlab.frontend.presentation

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


private const val MAX_HEART_RATE = 190
private const val SIMULATION_DELAY_MS = 2000L

private val zone1Color = Color(0xFF89AFDC)
private val zone2Color = Color(0xFF64BC46)
private val zone3Color = Color(0xFFF9C018)
private val zone4Color = Color(0xFFF36B24)
private val zone5Color = Color(0xFFEC2529)
private val defaultColor = Color.DarkGray

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

private fun formatElapsedTime(totalSeconds: Long): String {
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
fun HeartRateScreen(
    isAnimationEnabled: Boolean,
    isTimerRunning: Boolean,
    elapsedTimeSeconds: Long,
    onStopTimer: () -> Unit
) {
    var currentHeartRate by remember { mutableStateOf(75) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(SIMULATION_DELAY_MS)
            currentHeartRate = (60..165).random()
        }
    }

    val activeZoneColor = getZoneColor(currentHeartRate, MAX_HEART_RATE)
    val zoneColor = if (isAnimationEnabled) activeZoneColor else Color.Gray
    val progress = if (isAnimationEnabled)
        (currentHeartRate.toFloat() / MAX_HEART_RATE).coerceIn(0f, 1f)
    else 0f

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
                        text = "$currentHeartRate",
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
                            tint = zoneColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "bpm",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (isTimerRunning) {
                Text(
                    text = formatElapsedTime(elapsedTimeSeconds),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}