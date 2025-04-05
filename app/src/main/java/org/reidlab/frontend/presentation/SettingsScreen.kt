package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Settings", style = MaterialTheme.typography.title3)
            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { if (isTimerRunning) onStopTimer() else onStartTimer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isTimerRunning) MaterialTheme.colors.error else Color(0xFFAAE0FA),
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(if (isTimerRunning) "Stop Timer" else "Start Timer")
            }

        }
    }
}
