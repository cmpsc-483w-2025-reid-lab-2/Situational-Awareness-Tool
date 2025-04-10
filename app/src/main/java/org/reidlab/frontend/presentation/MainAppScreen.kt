package org.reidlab.frontend.presentation

import androidx.compose.runtime.*
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainAppScreen(birthDateString: String?) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    val birthDate: LocalDate? = remember(birthDateString) {
        birthDateString?.let { // If string is not null
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                null // Handle invalid stored string format gracefully
            }
        }
    }

    // State Variables
    // TODO: Consider loading initial values for toggles from SharedPreferences
    var isAnimationEnabled by remember { mutableStateOf(true) } // State for animation toggle, default on
    var showMilliseconds by remember { mutableStateOf(false) } // State for millisecond toggle, default off
    var isSimulationActive by remember { mutableStateOf(true) } // State for simulation toggle, default on
    var isHapticFeedbackEnabled by remember { mutableStateOf(true) } // State for haptic feedback toggle, default on

    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedTimeMillis by remember { mutableStateOf(0L) } // Changed to milliseconds
    var startTimeMillis by remember { mutableStateOf<Long?>(null) } // To store timer start time

    // Timer Logic
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

    // Main UI with Pager
    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            // Heart Rate Page
            0 -> HeartRateScreen(
                isAnimationEnabled = isAnimationEnabled,           // Pass animation state
                isTimerRunning = isTimerRunning,                   // Pass Timer state
                elapsedTimeMillis = elapsedTimeMillis,             // Pass milliseconds
                isHapticFeedbackEnabled = isHapticFeedbackEnabled, // Pass haptic feedback
                showMilliseconds = showMilliseconds,               // Pass toggle state
                isSimulationActive = isSimulationActive,           // Pass simulation state
                onStopTimer = stopTimer,
                birthDate = birthDate
            )
            // Settings Page
            1 -> SettingsScreen(
                isAnimationEnabled = isAnimationEnabled,                   // Pass current state
                onToggleAnimation = { isAnimationEnabled = it },           // Pass lambda to update state

                showMilliseconds = showMilliseconds,                       // Pass current state
                onToggleMilliseconds = { showMilliseconds = it },          // Pass lambda to update state

                isHapticFeedbackEnabled = isHapticFeedbackEnabled,         // Pass current state
                onToggleHapticFeedback = { isHapticFeedbackEnabled = it }, // Pass lambda to update state

                isSimulationActive = isSimulationActive,                    // Pass current simulation state
                onToggleSimulation = { isSimulationActive = it },           // Pass lambda to update simulation state

                isTimerRunning = isTimerRunning,
                onStartTimer = startTimer,
                onStopTimer = stopTimer
            )
        }
    }
}