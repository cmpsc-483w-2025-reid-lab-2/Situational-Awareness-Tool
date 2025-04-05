package org.reidlab.frontend.presentation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.rememberScalingLazyListState


@Composable
fun SettingsScreen(
    isAnimationEnabled: Boolean,
    onToggleAnimation: (Boolean) -> Unit,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 0.2.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            item {
                Text("Settings", style = MaterialTheme.typography.title3)
            }
            item {
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
            }
            item {
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
}
