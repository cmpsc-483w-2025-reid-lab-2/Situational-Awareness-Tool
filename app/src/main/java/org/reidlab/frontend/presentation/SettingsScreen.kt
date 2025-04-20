package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import org.reidlab.frontend.presentation.SessionCompleteDialog
import org.reidlab.frontend.R


@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    showMilliseconds: Boolean,
    onToggleMilliseconds: (Boolean) -> Unit,
    isSimulationActive: Boolean,
    onToggleSimulation: (Boolean) -> Unit,
    isHapticFeedbackEnabled: Boolean,
    onToggleHapticFeedback: (Boolean) -> Unit
) {
    var showSessionCompleteDialog by remember { mutableStateOf(false) }
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
                onClick = { if (isTimerRunning) {
                    onStopTimer()
                    showSessionCompleteDialog = true
                } else {
                    onStartTimer()
                } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isTimerRunning) MaterialTheme.colors.error else Color(0xFFAAE0FA),
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(if (isTimerRunning) "Stop Timer" else "Start Timer")
            }
            // Toggle Chip (Show milliseconds)
            ToggleChip(
                // Use the correct state variable for checked status
                checked = showMilliseconds,
                // Use the correct callback function when the chip is toggled
                onCheckedChange = onToggleMilliseconds,
                label = { Text("Show Milliseconds") },
                toggleControl = {
                    Switch(
                        // Also use the correct state variable here for the Switch visual
                        checked = showMilliseconds,
                        // Keep this null - the ToggleChip's onCheckedChange handles the logic
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFFAAE0FA), // Or your desired 'on' color
                            checkedThumbColor = Color.White,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = solidChipBackgroundColor, // Solid color when checked
                    checkedEndBackgroundColor = solidChipBackgroundColor,   // Solid color when checked
                )
            )
            // Toggle Chip (Enable Haptic Feedback)
            ToggleChip(
                // Use the new haptic feedback state
                checked = isHapticFeedbackEnabled,
                // Use the correct callback function when the chip is toggled
                onCheckedChange = onToggleHapticFeedback,
                label = { Text("Enable Haptic Feedback") },
                toggleControl = {
                    Switch(
                        // Also use the correct state variable here for the Switch visual
                        checked = isHapticFeedbackEnabled,
                        // Keep this null - the ToggleChip's onCheckedChange handles the logic
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFFAAE0FA),
                            checkedThumbColor = Color.White,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = solidChipBackgroundColor, // Solid color when checked
                    checkedEndBackgroundColor = solidChipBackgroundColor,   // Solid color when checked
                )
            )
            // Toggle Chip (Heart Rate Simulation)
            ToggleChip(
                checked = isSimulationActive,          // Use the new state
                onCheckedChange = onToggleSimulation, // Use the new callback
                label = { Text("Heart Rate Simulation") }, // Descriptive label
                toggleControl = {
                    Switch(
                        checked = isSimulationActive,      // Bind switch visual to state
                        onCheckedChange = null,           // ToggleChip handles clicks
                        colors = SwitchDefaults.colors(   // Consistent styling
                            checkedTrackColor = Color(0xFFAAE0FA),
                            checkedThumbColor = Color.White,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ToggleChipDefaults.toggleChipColors( // Consistent styling
                    checkedStartBackgroundColor = solidChipBackgroundColor,
                    checkedEndBackgroundColor = solidChipBackgroundColor,
                    uncheckedStartBackgroundColor = solidChipBackgroundColor,
                    uncheckedEndBackgroundColor = solidChipBackgroundColor
                )
            )
            // Toggle Chip (Heart Rate Animation)
            ToggleChip(
                checked = isAnimationEnabled,
                onCheckedChange = onToggleAnimation,
                label = { Text("Heart Beat Animation") },
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
        }
        SessionCompleteDialog(
            showDialog = showSessionCompleteDialog, // Control visibility with state
            onDismissRequest = { showSessionCompleteDialog = false }, // Hide on dismiss
            onAcceptClick = { showSessionCompleteDialog = false }    // Hide on accept
        )
    }
}