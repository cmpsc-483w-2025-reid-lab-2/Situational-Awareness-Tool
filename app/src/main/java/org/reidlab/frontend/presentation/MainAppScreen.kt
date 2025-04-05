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
    var isAnimationEnabled by remember { mutableStateOf(true) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedTimeSeconds by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L)
                elapsedTimeSeconds++
            }
        }
    }

    val startTimer: () -> Unit = {
        elapsedTimeSeconds = 0L
        isTimerRunning = true
        scope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    val stopTimer: () -> Unit = {
        isTimerRunning = false
    }

    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            0 -> HeartRateScreen(
                isAnimationEnabled = isAnimationEnabled,
                isTimerRunning = isTimerRunning,
                elapsedTimeSeconds = elapsedTimeSeconds,
                onStopTimer = stopTimer
            )
            1 -> SettingsScreen(
                isAnimationEnabled = isAnimationEnabled,
                onToggleAnimation = { isAnimationEnabled = it },
                isTimerRunning = isTimerRunning,
                onStartTimer = startTimer, // Pass the lambda
                onStopTimer = stopTimer
            )
        }
    }
}