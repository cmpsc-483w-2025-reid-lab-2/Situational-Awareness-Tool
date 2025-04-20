package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import org.reidlab.frontend.R

// Custom Wear OS Colors
private val CustomWearColors =
  Colors(
    primary = Color(0xFFC1E1C1), // Light Green (Accept Button)
    onPrimary = Color.Black, // Black (Checkmark on Accept)
    secondary = Color(0xFFCF6679), // Red (Decline Button)
    onSecondary = Color.White, // White (X on Decline)
    background = Color.Black, // Black (App Background)
    onBackground = Color.White, // White (Text on Background)
    surface = Color(0xFF1E1E1E), // Dark Grey (for Surfaces)
    onSurface = Color.White, // White (Text on Surfaces)
    error = Color(0xFFCF6679),
    onError = Color.Black
  )

@Composable
fun CustomTheme(content: @Composable () -> Unit) {
  MaterialTheme(colors = CustomWearColors, content = content)
}

@Composable
fun AcceptDeclineButtons(onAccept: () -> Unit, onDecline: () -> Unit) {
  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Button(
        onClick = onDecline,
        colors =
          ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
          ),
        modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = "Decline",
          modifier = Modifier.size(24.dp).wrapContentSize(align = Alignment.Center)
        )
      }

      Button(
        onClick = onAccept,
        colors =
          ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
          ),
        modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
      ) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = "Accept",
          modifier = Modifier.size(24.dp).wrapContentSize(align = Alignment.Center)
        )
      }
    }
  }
}

@Composable
fun LicenseAgreementScreen(
  onAccept: () -> Unit,
  onDecline: () -> Unit,
  headerTextSize: TextUnit = 16.5.sp,
  bodyTextSize: TextUnit = 13.sp
) {
  val scrollState = rememberScrollState()
  val focusRequester = remember { FocusRequester() }

  // Automatically request focus when the screen is first shown
  LaunchedEffect(Unit) { focusRequester.requestFocus() }

  Column(
    modifier =
      Modifier.fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 0.dp)
        // Order might matter, often input modifiers wrap scroll:
        .verticalScroll(scrollState) // Still needed to *do* the scrolling
        .rotaryScrollable( // Apply rotaryScrollable *after* verticalScroll
          // scrollState = scrollState, // Behavior now takes the state
          behavior =
            RotaryScrollableDefaults.behavior(
              scrollableState = scrollState
            ), // Provide the behavior
          focusRequester = focusRequester, // Pass requester here
          reverseDirection = false
          // overscrollEffect = null // Optional
        ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = stringResource(R.string.eula_header),
      style =
        TextStyle(
          fontSize = headerTextSize,
          fontFamily = MaterialTheme.typography.title2.fontFamily,
          fontWeight = MaterialTheme.typography.title2.fontWeight,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colors.onBackground
        ),
      modifier = Modifier.padding(bottom = 8.dp, top = 25.dp)
    )

    Text(
      text = stringResource(R.string.eula_body),
      style =
        TextStyle(
          fontSize = bodyTextSize,
          fontFamily = MaterialTheme.typography.body2.fontFamily,
          fontWeight = MaterialTheme.typography.body2.fontWeight,
          textAlign = TextAlign.Left,
          color = MaterialTheme.colors.onBackground
        ),
      modifier = Modifier.padding(bottom = 16.dp)
    )

    AcceptDeclineButtons(onAccept = onAccept, onDecline = onDecline)
    Spacer(modifier = Modifier.height(16.dp))
  }
}
