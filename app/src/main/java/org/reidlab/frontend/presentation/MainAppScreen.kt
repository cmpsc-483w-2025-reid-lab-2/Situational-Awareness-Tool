package org.reidlab.frontend.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.reidlab.frontend.data.HealthServicesRepository


@Composable
fun MainAppScreen() {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    // ViewModel Setup
    val context = LocalContext.current
    // Remember repository and factory to avoid recreation on recomposition
    val healthServicesRepository = remember { HealthServicesRepository(context) }
    val viewModelFactory = remember { MeasureDataViewModelFactory(healthServicesRepository) }
    // Obtain the ViewModel instance
    val measureDataViewModel: MeasureDataViewModel = viewModel(factory = viewModelFactory)


    // State Variables (remain the same)
    var isAnimationEnabled by remember { mutableStateOf(true) }
    var showMilliseconds by remember { mutableStateOf(false) }
    var isSimulationActive by remember { mutableStateOf(false) }
    var isHapticFeedbackEnabled by remember { mutableStateOf(true) }

    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedTimeMillis by remember { mutableLongStateOf(0L) }
    var startTimeMillis by remember { mutableStateOf<Long?>(null) }

    // Timer Logic (remains the same)
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            val start = startTimeMillis ?: System.currentTimeMillis().also { startTimeMillis = it }
            while (isTimerRunning) {
                elapsedTimeMillis = System.currentTimeMillis() - start
                delay(100L)
            }
        }
    }

    // Timer Control Lambdas (remain the same)
    val startTimer: () -> Unit = {
        startTimeMillis = System.currentTimeMillis()
        elapsedTimeMillis = 0L
        isTimerRunning = true
        scope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    // A value for stopTimer()
    val stopTimer: () -> Unit = {
        isTimerRunning = false
    }

    // Main UI with Pager
    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            // Heart Rate Page
            0 -> HeartRateScreen(
                // Pass the ViewModel instance
                measureDataViewModel = measureDataViewModel,
                // Other parameters remain the same
                isAnimationEnabled = isAnimationEnabled,
                isTimerRunning = isTimerRunning,
                elapsedTimeMillis = elapsedTimeMillis,
                isHapticFeedbackEnabled = isHapticFeedbackEnabled,
                showMilliseconds = showMilliseconds,
                isSimulationActive = isSimulationActive,
                onStopTimer = stopTimer
            )
            // Settings Page (remains the same)
            1 -> SettingsScreen(
                isAnimationEnabled = isAnimationEnabled,
                onToggleAnimation = { isAnimationEnabled = it },

                showMilliseconds = showMilliseconds,
                onToggleMilliseconds = { showMilliseconds = it },

                isHapticFeedbackEnabled = isHapticFeedbackEnabled,
                onToggleHapticFeedback = { isHapticFeedbackEnabled = it },

                isSimulationActive = isSimulationActive,
                onToggleSimulation = { isSimulationActive = it },

                isTimerRunning = isTimerRunning,
                onStartTimer = startTimer,
                onStopTimer = stopTimer
            )
        }
    }
}