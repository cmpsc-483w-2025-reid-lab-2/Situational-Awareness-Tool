package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.composables.DatePicker
import java.time.LocalDate

@Composable
fun BirthdayScreen(
    onDateSelectedAndValid: (LocalDate) -> Unit // Callback with the valid selected date
) {
    // Date Calculation
    val today = LocalDate.now()
    val maxDate = remember { today.minusYears(18) }
    val minDate = remember { today.minusYears(100) }
    val defaultDate = remember {
        val default = LocalDate.of(1990, 1, 1)
        default.coerceIn(minDate, maxDate)
    }

    // Define Custom Colors
    val pickerPrimaryBlue = Color(0xffa8e0fa)
    val pickerPrimaryBlueVariant = Color(0xffa8e0fa)

    // Get Current Theme Colors
    val currentAppColors = MaterialTheme.colors

    // Override the default colors provided by Horologist library
    val pickerSpecificColors = currentAppColors.copy(
        // Primary colors (used for main actions like confirm button)
        primary = pickerPrimaryBlue,
        primaryVariant = pickerPrimaryBlueVariant,

        // Secondary colors (often used for selection, sliders, highlights)
        secondary = pickerPrimaryBlue,
        secondaryVariant = pickerPrimaryBlueVariant,
    )

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp, top = 4.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(2.dp))

        // Apply the highly customized theme colors *only* to the DatePicker
        MaterialTheme(colors = pickerSpecificColors) {
            DatePicker(
                onDateConfirm = onDateSelectedAndValid,
                date = defaultDate,
                fromDate = minDate,
                toDate = maxDate
             )
        }
    }
}