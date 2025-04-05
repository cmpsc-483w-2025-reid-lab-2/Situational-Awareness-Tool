package org.reidlab.frontend.presentation

import androidx.compose.runtime.*
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainAppScreen() {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    // State Variables
    // TODO: Consider loading initial values for toggles from SharedPreferences
    var isAnimationEnabled by remember { mutableStateOf(true) }
    var showMilliseconds by remember { mutableStateOf(false) } // State for millisecond toggle, default off

    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedTimeMillis by remember { mutableStateOf(0L) } // Changed to milliseconds
    var startTimeMillis by remember { mutableStateOf<Long?>(null) } // To store timer start time

    // Timer Logic
    // LaunchedEffect now calculates elapsed time based on system time
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            val start = startTimeMillis ?: System.currentTimeMillis().also { startTimeMillis = it } // Get start time, record if missing
            while (isTimerRunning) { // Loop only while running
                elapsedTimeMillis = System.currentTimeMillis() - start
                delay(100L) // Update frequency for the display (e.g., every 100ms)
            }
        }
    }

    // Timer Control Lambdas
    val startTimer: () -> Unit = {
        startTimeMillis = System.currentTimeMillis() // Record start time
        elapsedTimeMillis = 0L // Reset elapsed display
        isTimerRunning = true
        // Navigate back to HeartRateScreen when starting timer
        scope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    val stopTimer: () -> Unit = {
        isTimerRunning = false
        // startTimeMillis = null
    }

    // Main UI of the Settings Page
    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            // Heart Rate Page
            0 -> HeartRateScreen(
                isAnimationEnabled = isAnimationEnabled,
                isTimerRunning = isTimerRunning,
                elapsedTimeMillis = elapsedTimeMillis,      // Pass milliseconds
                showMilliseconds = showMilliseconds,        // Pass toggle state
                onStopTimer = stopTimer
            )
            // Settings Page
            1 -> SettingsScreen(
                isAnimationEnabled = isAnimationEnabled,         // Pass current state
                onToggleAnimation = { isAnimationEnabled = it }, // Pass lambda to update state

                // Add these parameters to SettingsScreen signature
                showMilliseconds = showMilliseconds,              // Pass current state
                onToggleMilliseconds = { showMilliseconds = it }, // Pass lambda to update state

                isTimerRunning = isTimerRunning,
                onStartTimer = startTimer,
                onStopTimer = stopTimer
            )
        }
    }
}