package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.* // Provides Scaffold, TimeText, etc.
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// --- Imports for Scrolling & Rotary Input ---
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults

import org.reidlab.frontend.R

@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    showMilliseconds: Boolean,
    onToggleMilliseconds: (Boolean) -> Unit
) {
    // Add ScrollState and FocusRequester
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val solidChipBackgroundColor = MaterialTheme.colors.surface.copy(alpha = 1.0f)

    Scaffold(
        timeText = {
            TimeText()
        },
    ) {
        // Placed inside Scaffold content lambda but outside Column
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        // Apply scrolling and rotary modifiers to the Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Define all the parameters of the padding for fine tuned spacings
                .padding(start = 10.dp, end = 10.dp, top = 27.dp, bottom = 0.dp)
                // Apply verticalScroll and rotaryScrollable modifiers
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
            // Content Items
            // Settings Title
            Text(
                "Settings",
                style = MaterialTheme.typography.title3,
                fontSize = 16.sp, // Explicit size override
            )

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
            // Toggle Chip 1 (Heart Rate Animation)
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
                modifier = Modifier.fillMaxWidth(),
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = solidChipBackgroundColor, // Solid color when checked
                    checkedEndBackgroundColor = solidChipBackgroundColor,   // Solid color when checked
                    uncheckedStartBackgroundColor = solidChipBackgroundColor, // Solid color when unchecked
                    uncheckedEndBackgroundColor = solidChipBackgroundColor  // Solid color when unchecked
                )
            )

            // Toggle Chip (Show milliseconds)
            ToggleChip(
                // Use the correct state variable for checked status
                checked = showMilliseconds,
                // Use the correct callback function when the chip is toggled
                onCheckedChange = onToggleMilliseconds,
                label = { Text("Show milliseconds") },
                toggleControl = {
                    Switch(
                        // Also use the correct state variable here for the Switch visual
                        checked = showMilliseconds,
                        // Keep this null - the ToggleChip's onCheckedChange handles the logic
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFFAAE0FA), // Or your desired 'on' color
                            checkedThumbColor = Color.White,
                            // Optional: Define colors for the 'off' state too
                            uncheckedTrackColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                            uncheckedThumbColor = MaterialTheme.colors.surface
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = solidChipBackgroundColor, // Solid color when checked
                    checkedEndBackgroundColor = solidChipBackgroundColor,   // Solid color when checked
                    uncheckedStartBackgroundColor = solidChipBackgroundColor, // Solid color when unchecked
                    uncheckedEndBackgroundColor = solidChipBackgroundColor  // Solid color when unchecked
                )
            )
            // App Name & Version & Author Text
            Text(
                text = "${stringResource(R.string.app_name)}\n${stringResource(R.string.app_version)} | ${stringResource(R.string.author_name)}",
                fontSize = 11.sp,
                // Use a muted color so it's less prominent
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                // Ensure it spans the width for centering to work correctly
                modifier = Modifier.fillMaxWidth()
            )
            // Add some end padding so the button isn't cut off
            Spacer(Modifier.height(35.dp))
            // End Content Items
        } // End Column
    } // End Scaffold
}