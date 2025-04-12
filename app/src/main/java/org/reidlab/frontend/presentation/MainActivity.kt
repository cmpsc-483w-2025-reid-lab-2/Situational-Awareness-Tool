package org.reidlab.frontend.presentation

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.format.DateTimeFormatter


private const val PREFS_NAME = "AppPrefs_HeartRateMonitor"
private const val KEY_LICENSE_ACCEPTED = "license_accepted_v1"
private const val KEY_BIRTH_DATE_STR = "birth_date_str_v1"

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault) // Consider if you want this or just CustomTheme

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val previouslyAcceptedLicense = prefs.getBoolean(KEY_LICENSE_ACCEPTED, false)
        val storedBirthDateString = prefs.getString(KEY_BIRTH_DATE_STR, null)

        setContent {
            // --- State Management ---
            // 1. EULA State
            var licenseAccepted by remember { mutableStateOf(previouslyAcceptedLicense) }
            // 2. Birthday State
            var birthDateString by remember { mutableStateOf(storedBirthDateString) }
            // 3. Permission State
            val bodySensorPermissionState = rememberPermissionState(
                Manifest.permission.BODY_SENSORS
            )
            // Initialize based on current permission status
            var permissionGranted by remember { mutableStateOf(bodySensorPermissionState.status.isGranted) }

            // Effect to update permission state if changed externally (e.g., in settings)
            LaunchedEffect(bodySensorPermissionState.status) {
                permissionGranted = bodySensorPermissionState.status.isGranted
                // Optional: Handle revocation explicitly if needed
                // if (!bodySensorPermissionState.status.isGranted) {
                //    permissionGranted = false
                // }
            }

            CustomTheme {
                // --- Navigation Logic ---
                when {
                    // 1. EULA Page First
                    !licenseAccepted -> {
                        LicenseAgreementScreen(
                            onAccept = {
                                prefs.edit { putBoolean(KEY_LICENSE_ACCEPTED, true) }
                                licenseAccepted = true // Trigger recomposition
                            },
                            onDecline = {
                                finish() // Exit the app if declined
                            }
                        )
                    }

                    // 2. Then Birthday Picker (if EULA accepted)
                    birthDateString == null -> {
                        BirthdayScreen(
                            onDateSelectedAndValid = { selectedDate ->
                                // Format and save the valid birth date
                                val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                prefs.edit { putString(KEY_BIRTH_DATE_STR, dateStr) }
                                birthDateString = dateStr // Trigger recomposition
                            }
                        )
                    }

                    // 3. Then Body Sensor Permissions (if EULA accepted and Birthday entered)
                    !permissionGranted -> {
                        PermissionsScreen(
                            onPermissionGranted = {
                                // State is updated by the LaunchedEffect watching bodySensorPermissionState
                                // You might not strictly need this callback anymore if PermissionsScreen
                                // uses bodySensorPermissionState.launchPermissionRequest() directly.
                                // However, keeping it can be useful for triggering actions *immediately*
                                // after the user grants within the app's flow. We'll update our state anyway.
                                permissionGranted = true
                            },
                            onPermissionDenied = {
                                // Decide what to do if denied: finish, show explanation, etc.
                                finish() // Exit the app if denied
                            }
                        )
                    }

                    // 4. Finally, Main App Screen (if all previous steps completed)
                    else -> {
                        MainAppScreen(birthDateString = birthDateString) // Pass the birth date string
                    }
                }
            }
        }
    }
}