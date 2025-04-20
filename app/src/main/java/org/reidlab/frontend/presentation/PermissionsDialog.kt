package org.reidlab.frontend.presentation

import android.Manifest
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.reidlab.frontend.R

// GrantDenyButtons composable remains the same as previously defined
@Composable
fun GrantDenyButtons(onGrant: () -> Unit, onDeny: () -> Unit) {
  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Button(
        onClick = onDeny,
        colors =
          ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
          ),
        modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
      ) {
        Icon(Icons.Filled.Close, stringResource(R.string.deny_permission), Modifier.size(24.dp))
      }
      Button(
        onClick = onGrant,
        colors =
          ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
          ),
        modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
      ) {
        Icon(Icons.Filled.Check, stringResource(R.string.grant_permission), Modifier.size(24.dp))
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class) // Opt-in for Accompanist Permissions
@Composable
fun PermissionsScreen(
  onPermissionGranted: () -> Unit,
  onPermissionDenied: () -> Unit, // Called if user presses Deny button OR denies in system dialog
  headerTextSize: TextUnit = 16.5.sp,
  bodyTextSize: TextUnit = 13.sp
) {
  val permission = Manifest.permission.BODY_SENSORS

  // Use rememberPermissionState from Accompanist
  val permissionState =
    rememberPermissionState(
      permission = permission,
      onPermissionResult = { isGranted ->
        // This is called AFTER the system dialog closes
        if (isGranted) {
          onPermissionGranted()
        } else {
          // User denied the permission in the system dialog.
          // You might want different behavior here than pressing the initial "Deny" button.
          // For now, we call the same denial callback.
          onPermissionDenied()
        }
      }
    )

  // This LaunchedEffect triggers the callback if the state is already Granted
  // when the composable enters the composition, or if it becomes Granted later
  // without going through the onPermissionResult lambda (e.g., granted via settings).
  LaunchedEffect(permissionState.status) {
    if (permissionState.status.isGranted) {
      onPermissionGranted()
    }
  }

  // Scroll and Focus state for Rotary Input
  val scrollState = rememberScrollState()
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) { focusRequester.requestFocus() }

  CustomTheme { // Apply the custom theme
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(horizontal = 24.dp, vertical = 0.dp)
          .verticalScroll(scrollState)
          .rotaryScrollable(
            behavior = RotaryScrollableDefaults.behavior(scrollableState = scrollState),
            focusRequester = focusRequester,
            reverseDirection = false
          ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header Text
      Text(
        text = stringResource(R.string.permission_header),
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

      // Modify the text or UI if permissionState.status.shouldShowRationale is true
      val explanationText = stringResource(R.string.permission_body)

      // Body Text explaining why permission is needed
      Text(
        text = explanationText,
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

      // Grant/Deny Buttons
      GrantDenyButtons(
        onGrant = {
          // Use the launchPermissionRequest function from PermissionState
          permissionState.launchPermissionRequest()
        },
        onDeny = {
          // User explicitly denied *without* seeing system dialog
          onPermissionDenied()
        }
      )

      Spacer(modifier = Modifier.height(16.dp)) // Spacer at the bottom
    }
  }
}
