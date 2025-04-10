package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.DatePicker
import java.time.LocalDate

@Composable
fun BirthdayScreen(
    onDateSelectedAndValid: (LocalDate) -> Unit // Callback with the valid selected date
) {
    // Calculate date range: 18 years ago to 100 years ago
    val today = LocalDate.now()
    // Latest selectable date (must be 18 years old)
    val maxDate = remember { today.minusYears(18) }
    // Earliest selectable date (e.g., 100 years ago)
    val minDate = remember { today.minusYears(100) }
    // Default date for picker, can be maxDate or something else reasonable
    val defaultDate = remember {
        val default = LocalDate.of(1990, 1, 1)
        // Make sure the default is not outside the allowed selectable range
        default.coerceIn(minDate, maxDate)
    }
    // --- Added: Define Blue Color and Create Local Theme ---
    // 1. Define the blue color you want for the picker
    val pickerPrimaryBlue = Color(0xFF8AB4F8) // Example Blue (same as before)
    // Optional: Define a darker variant if needed by component internals
    val pickerPrimaryBlueVariant = Color(0xFF4E85F3)
    // 2. Get the current theme's colors
    val currentAppColors = MaterialTheme.colors
    // 3. Create a *new* Colors object based on the current one, overriding primary
    val pickerSpecificColors = currentAppColors.copy(
        primary = pickerPrimaryBlue,
        primaryVariant = pickerPrimaryBlueVariant, // Or just pickerPrimaryBlue
        // Ensure text/icon on this blue is readable (might inherit from currentAppColors.onPrimary or set explicitly)
        onPrimary = Color.Black, // Adjust if needed based on your blue's brightness
    )



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 4.dp, top = 0.dp, bottom = 5.dp), // Adjusted padding slightly
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Birth Date",
            style = MaterialTheme.typography.title3,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            // Show the latest allowed year clearly
            text = "(Must be ${maxDate.year} or earlier)",
            style = MaterialTheme.typography.caption1,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))

        // Apply the modified colors only to the composables inside this block
        MaterialTheme(colors = pickerSpecificColors) {
            DatePicker(
                onDateConfirm = onDateSelectedAndValid,
                date = defaultDate,
                fromDate = minDate,
                toDate = maxDate
                // The DatePicker will now use pickerSpecificColors.primary
                // Note: Internal text size still cannot be changed here
            )
        } // --- End Local Theme ---
    }
}