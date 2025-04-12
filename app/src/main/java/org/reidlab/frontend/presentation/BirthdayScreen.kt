package org.reidlab.frontend.presentation

import java.time.LocalDate
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.wrapContentSize 
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.composables.DatePicker


@Composable
fun BirthdayScreen(
    onDateSelectedAndValid: (LocalDate) -> Unit // Callback with the valid selected date
) {
    var showDialog by remember { mutableStateOf(true) }
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

    // Initial Dialog
    Dialog(
        showDialog = showDialog,
        onDismissRequest = { /* Consider behavior */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Adjust padding if needed, especially vertical if text wraps more
                .padding(vertical = 10.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Adjust spacing between title, body, and button
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Input Your Birthday", // New title
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.title3,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.fillMaxWidth() // Removed top padding
            )

            Text(
                text = "Your birth date helps calculate accurate heart rates and heart rate zones.",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2, // Use a smaller style for body
                color = MaterialTheme.colors.onSurfaceVariant, // Slightly different color for contrast
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showDialog = false },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFAAE0FA)
                ),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Accept",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        }
    }


    // UI Layout
    if (!showDialog) {
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
}