package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.* // Provides Scaffold, TimeText, etc.
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// Imports for Scrolling & Rotary Input
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults


@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    // 1. Add ScrollState and FocusRequester
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        timeText = {
            TimeText()
        },
        vignette = { } // Vignette disabled as before
        // Note: PositionIndicator doesn't directly support ScrollState.
        // To add a scrollbar easily, consider using ScalingLazyColumn instead of Column.
    ) {
        // 2. Request focus when the screen launches
        // Placed inside Scaffold content lambda but outside Column
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        // Apply scrolling and rotary modifiers to the Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Keep existing padding
                .padding(horizontal = 16.dp, vertical = 27.dp)
                // 3. Apply verticalScroll and rotaryScrollable modifiers
                .verticalScroll(scrollState) // Make Column scrollable
                .rotaryScrollable(            // Handle rotary input
                    behavior = RotaryScrollableDefaults.behavior(scrollableState = scrollState),
                    focusRequester = focusRequester,
                    reverseDirection = false
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Consider using Arrangement.spacedBy() for consistent spacing
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            // --- Content Items ---
            // Settings Title
            Text(
                "Settings",
                style = MaterialTheme.typography.title3,
                fontSize = 16.sp, // Explicit size override
            )
            // Removed Spacer - Arrangement.spacedBy handles spacing now

            // Toggle Chip
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
            // Removed Spacer

            // Start/Stop Button
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
            // --- End Content Items ---
        } // End Column
    } // End Scaffold
}