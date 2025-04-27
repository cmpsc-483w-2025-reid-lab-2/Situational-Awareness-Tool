// SPDX-License-Identifier: MIT
// Copyright (c) 2025 REID Lab 2

package org.reidlab.frontend.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun FrontendTheme(content: @Composable () -> Unit) {
  /**
   * Empty theme to customize for your app. See:
   * https://developer.android.com/jetpack/compose/designsystems/custom
   */
  MaterialTheme(content = content)
}
