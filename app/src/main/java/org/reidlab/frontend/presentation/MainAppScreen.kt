package org.reidlab.frontend.presentation

import androidx.compose.runtime.*
import com.google.accompanist.pager.*
import androidx.wear.compose.material.*
import androidx.compose.foundation.layout.*
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainAppScreen() {
    val pagerState = rememberPagerState(initialPage = 0)
    var isAnimationEnabled by remember { mutableStateOf(true) }

    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            0 -> HeartRateScreen(isAnimationEnabled = isAnimationEnabled)
            1 -> SettingsScreen(
                isAnimationEnabled = isAnimationEnabled,
                onToggleAnimation = { isAnimationEnabled = it }
            )
        }
    }
}
