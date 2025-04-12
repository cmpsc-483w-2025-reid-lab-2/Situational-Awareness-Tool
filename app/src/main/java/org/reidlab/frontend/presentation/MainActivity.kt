package org.reidlab.frontend.presentation

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.content.edit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

private const val PREFS_NAME = "AppPrefs_HeartRateMonitor"
private const val KEY_LICENSE_ACCEPTED = "license_accepted_v1"

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val previouslyAcceptedLicense = prefs.getBoolean(KEY_LICENSE_ACCEPTED, false)

        setContent {
            // State for tracking license acceptance
            var licenseAccepted by remember { mutableStateOf(previouslyAcceptedLicense) }
            // State for tracking whether the permission screen flow has resulted in a grant
            // We initialize based on the *current* permission status
            val bodySensorPermissionState = rememberPermissionState(
                Manifest.permission.BODY_SENSORS
            )
            var permissionGranted by remember { mutableStateOf(bodySensorPermissionState.status.isGranted) }

            // This effect ensures that if the permission is granted *outside* the explicit
            // request flow (e.g., already granted, or granted via settings),
            // our state is updated correctly.
            LaunchedEffect(bodySensorPermissionState.status) {
                if (bodySensorPermissionState.status.isGranted) {
                    permissionGranted = true
                }
                // Optional: Handle cases where permission is revoked in settings while app is running
                // else {
                //    permissionGranted = false
                // }
            }

            CustomTheme {
                // Navigation Logic
                when {
                    // 1. If license is not accepted, show license screen
                    !licenseAccepted -> {
                        LicenseAgreementScreen(
                            onAccept = {
                                prefs.edit { putBoolean(KEY_LICENSE_ACCEPTED, true) }
                                licenseAccepted = true
                                // When this runs, licenseAccepted becomes true
                            },
                            onDecline = {
                                finish()
                            }
                        )
                    } // The code ONLY moves past here if licenseAccepted is TRUE

                    // 2. If license IS accepted (because the first condition was false),
                    //    AND permission is not granted, show permission screen
                    !permissionGranted -> {
                        PermissionsScreen( // <--- This screen is ONLY shown if !licenseAccepted was false
                            onPermissionGranted = {
                                permissionGranted = true
                            },
                            onPermissionDenied = {
                                finish()
                            }
                        )
                    } // The code ONLY moves past here if permissionGranted is TRUE

                    // 3. If license IS accepted AND permission IS granted, show main app
                    else -> {
                        MainAppScreen()
                    }
                }
            }
        }
    }
}
