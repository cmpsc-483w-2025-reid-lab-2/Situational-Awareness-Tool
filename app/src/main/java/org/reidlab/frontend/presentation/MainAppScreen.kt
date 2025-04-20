package org.reidlab.frontend.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.reidlab.frontend.data.HealthServicesRepository

@Composable
fun MainAppScreen(birthDateString: String?) {
  val pagerState = rememberPagerState(initialPage = 0)
  val scope = rememberCoroutineScope()

  // ViewModel Setup
  val context = LocalContext.current
  // Remember repository and factory to avoid recreation on recomposition
  val healthServicesRepository = remember { HealthServicesRepository(context) }
  val viewModelFactory = remember { MeasureDataViewModelFactory(healthServicesRepository) }
  // Obtain the ViewModel instance
  val measureDataViewModel: MeasureDataViewModel = viewModel(factory = viewModelFactory)

  // State Variables
  // TODO: Consider loading initial values for toggles from SharedPreferences
  var isAnimationEnabled by remember { mutableStateOf(true) }
  var showMilliseconds by remember { mutableStateOf(false) }
  var isSimulationActive by remember { mutableStateOf(false) }
  var isHapticFeedbackEnabled by remember { mutableStateOf(true) }
  var isTimerRunning by remember { mutableStateOf(false) }
  var elapsedTimeMillis by remember { mutableLongStateOf(0L) }
  var startTimeMillis by remember { mutableStateOf<Long?>(null) }

  val birthDate: LocalDate? =
    remember(birthDateString) {
      birthDateString?.let { // If string is not null
        try {
          LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
          null // Handle invalid stored string format gracefully
        }
      }
    }

  // Timer Logic
  LaunchedEffect(key1 = isTimerRunning) {
    if (isTimerRunning) {
      val start = startTimeMillis ?: System.currentTimeMillis().also { startTimeMillis = it }
      while (isTimerRunning) {
        elapsedTimeMillis = System.currentTimeMillis() - start
        delay(100L)
      }
    }
  }

  // Timer Control Lambdas
  val startTimer: () -> Unit = {
    startTimeMillis = System.currentTimeMillis()
    elapsedTimeMillis = 0L
    isTimerRunning = true
    scope.launch { pagerState.animateScrollToPage(0) }
  }

  // A value for stopTimer()
  val stopTimer: () -> Unit = { isTimerRunning = false }

  // Main UI with Pager
  HorizontalPager(count = 2, state = pagerState) { page ->
    when (page) {
      // Heart Rate Page
      0 ->
        HeartRateScreen(
          // Pass the ViewModel instance
          measureDataViewModel = measureDataViewModel,
          // Other parameters remain the same
          isAnimationEnabled = isAnimationEnabled,
          isTimerRunning = isTimerRunning,
          elapsedTimeMillis = elapsedTimeMillis,
          isHapticFeedbackEnabled = isHapticFeedbackEnabled,
          showMilliseconds = showMilliseconds,
          isSimulationActive = isSimulationActive,
          onStopTimer = stopTimer,
          birthDate = birthDate
        )
      // Settings Page (remains the same)
      1 ->
        SettingsScreen(
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
