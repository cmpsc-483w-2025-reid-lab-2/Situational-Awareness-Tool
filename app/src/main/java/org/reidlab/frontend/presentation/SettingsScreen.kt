package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.* // Provides Scaffold, TimeText, etc.
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    // Scaffold now includes the timeText parameter
    Scaffold(
        timeText = {
            // Add the TimeText composable here
            TimeText()
        },
        // Add this line to disable the default Vignette
        vignette = { }
        // You can also add other Scaffold elements like positionIndicator if needed
        // positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        // Your main screen content goes inside the Scaffold's content lambda
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Padding might need adjustment depending on visual preference with TimeText
                .padding(horizontal = 16.dp, vertical = 27.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Content starts from the top of the Column area
        ) {
            // Your "Settings" text - Note: applying both style and fontSize like this
            // overrides the fontSize from the style. Consider using just fontSize
            // or a smaller style like caption1 directly if that's the goal.
            Text(
                "Settings",
                style = MaterialTheme.typography.title3, // Base style used
                fontSize = 16.sp, // Font size is explicitly overridden here
            )
            Spacer(modifier = Modifier.height(8.dp))

            ToggleChip(
                checked = isAnimationEnabled,
                onCheckedChange = onToggleAnimation,
                label = { Text("Heart Rate Animation") },
                toggleControl = {
                    Switch(
                        checked = isAnimationEnabled,
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFFAAE0FA),
                            checkedThumbColor = Color.White
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { if (isTimerRunning) onStopTimer() else onStartTimer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isTimerRunning) MaterialTheme.colors.error else Color(
                        0xFFAAE0FA
                    ),
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(if (isTimerRunning) "Stop Timer" else "Start Timer")
            }
        } // End Column
    } // End Scaffold
}