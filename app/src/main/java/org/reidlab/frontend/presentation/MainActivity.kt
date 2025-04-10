/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package org.reidlab.frontend.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val PREFS_NAME = "AppPrefs_HeartRateMonitor"
private const val KEY_LICENSE_ACCEPTED = "license_accepted_v1"
private const val KEY_BIRTH_DATE_STR = "birth_date_str_v1"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val previouslyAccepted = prefs.getBoolean(KEY_LICENSE_ACCEPTED, false)
        val storedBirthDateString = prefs.getString(KEY_BIRTH_DATE_STR, null)

        setContent {
            CustomTheme {
                var accepted by remember { mutableStateOf(previouslyAccepted) }
                // --- State holds the birth date string ---
                var birthDateString by remember { mutableStateOf(storedBirthDateString) }

                // --- Navigation Logic ---
                when {
                    // 1. If license not accepted, show license screen
                    !accepted -> {
                        LicenseAgreementScreen(
                            onAccept = {
                                prefs.edit { putBoolean(KEY_LICENSE_ACCEPTED, true) }
                                accepted = true
                            },
                            onDecline = { finish() }
                        )
                    }
                    // 2. If license accepted BUT birth date not stored, show birthday screen
                    birthDateString == null -> {
                        BirthdayScreen(
                            onDateSelectedAndValid = { selectedDate ->
                                // Format the selected date (already known to be >= 18)
                                val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                // Save the date string to SharedPreferences
                                prefs.edit { putString(KEY_BIRTH_DATE_STR, dateStr) }
                                // Update the local state to trigger recomposition/navigation
                                birthDateString = dateStr
                            }
                        )
                    }
                    // 3. If both accepted and birth date stored, show main app
                    else -> {
                        // --- Pass the stored birth date string to MainAppScreen ---
                        MainAppScreen(birthDateString = birthDateString)
                    }
                }
            }
        }
    }
}