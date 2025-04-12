package org.reidlab.frontend.presentation // Keeping your original package name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Needed for custom color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Make sure dialog imports are correct
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import java.time.DateTimeException // Needed for date validation
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check

// Explicit Wear Compose Material imports
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
// import androidx.wear.compose.material.TimeText // No longer needed
// import androidx.wear.compose.material.TimeTextDefaults // No longer needed
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState


@Composable
fun BirthdayScreen(
    onDateSelectedAndValid: (LocalDate) -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }

    // --- Date Range Calculation ---
    val today = remember { LocalDate.now() }
    val maxYear = remember { today.year - 18 }
    val minYear = remember { today.year - 100 }
    val years = remember { (minYear..maxYear).toList() }

    // --- State for selected values ---
    val initialYear = maxYear
    val initialMonthIndex = remember { today.monthValue - 1 }
    val initialDay = remember { today.dayOfMonth }

    val selectedYear = remember { mutableStateOf(initialYear) }
    val selectedMonthIndex = remember { mutableStateOf(initialMonthIndex) }
    val selectedDay = remember { mutableStateOf(initialDay) }

    // --- Picker States ---
    val yearState = rememberPickerState(
        initialNumberOfOptions = years.size,
        initiallySelectedOption = years.indexOf(initialYear).coerceAtLeast(0)
    )
    val monthState = rememberPickerState(
        initialNumberOfOptions = 12,
        initiallySelectedOption = initialMonthIndex
    )
    val dayState = rememberPickerState(
        initialNumberOfOptions = 31,
        initiallySelectedOption = initialDay - 1
    )

    // --- Data Lists for Pickers ---
    val months = remember {
        Month.values().map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }
    val days = remember { (1..31).map { String.format("%02d", it) } }

    // --- Derived state to check if the currently selected date is valid ---
    val isDateValid by remember {
        derivedStateOf {
            try {
                LocalDate.of(selectedYear.value, selectedMonthIndex.value + 1, selectedDay.value)
                true
            } catch (e: DateTimeException) {
                false
            }
        }
    }

    // --- Initial Dialog ---
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

            // Added Body Text
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

    // --- Main Picker Screen ---
    if (!showDialog) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            // Removed the timeText parameter from Scaffold
            Scaffold(
                vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
            ) { // Scaffold lambda takes no parameters

                // The Box receives the padding implicitly from Scaffold
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Keep other specific padding if needed
                        .padding(bottom = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp)
                    ) {

                        // --- Year Picker ---
                        Text(
                            text = "Year",
                            style = MaterialTheme.typography.caption1,
                            color = MaterialTheme.colors.onSurfaceVariant,
                        )
                        Picker(
                            state = yearState,
                            contentDescription = "Select Year",
                            modifier = Modifier.height(40.dp).fillMaxWidth(0.6f),
                            separation = 2.dp
                        ) { optionIndex ->
                            LaunchedEffect(yearState.selectedOption) {
                                selectedYear.value = years[yearState.selectedOption]
                            }
                            val year = years[optionIndex]
                            val isSelected = (year == selectedYear.value)
                            Text(
                                text = year.toString(),
                                maxLines = 1,
                                style = MaterialTheme.typography.title1,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            )
                        }

                        // --- Month & Day Pickers Row ---
                        Text(
                            text = "Month / Day",
                            style = MaterialTheme.typography.caption1,
                            color = MaterialTheme.colors.onSurfaceVariant,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(vertical = 0.dp)
                        ) {
                            Picker(
                                state = monthState,
                                contentDescription = "Select Month",
                                modifier = Modifier.weight(1f),
                                separation = 4.dp,
                            ) { optionIndex ->
                                LaunchedEffect(monthState.selectedOption) {
                                    selectedMonthIndex.value = monthState.selectedOption
                                }
                                val isSelected = (optionIndex == selectedMonthIndex.value)
                                Text(
                                    text = months[optionIndex],
                                    maxLines = 1,
                                    style = MaterialTheme.typography.display2,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                            }

                            Text(
                                text = ":",
                                style = MaterialTheme.typography.display2,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.onBackground,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            Picker(
                                state = dayState,
                                contentDescription = "Select Day",
                                modifier = Modifier.weight(1f),
                                separation = 4.dp,
                            ) { optionIndex ->
                                LaunchedEffect(dayState.selectedOption) {
                                    selectedDay.value = optionIndex + 1
                                }
                                val isSelected = (optionIndex == dayState.selectedOption - 1)
                                Text(
                                    text = days[optionIndex],
                                    maxLines = 1,
                                    style = MaterialTheme.typography.display2,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                            }
                        } // End Row

                        // --- Confirmation Button ---
                        Button(
                            onClick = {
                                try {
                                    val date = LocalDate.of(
                                        selectedYear.value,
                                        selectedMonthIndex.value + 1,
                                        selectedDay.value
                                    )
                                    onDateSelectedAndValid(date)
                                } catch (e: DateTimeException) {
                                    println("Attempted to confirm invalid date: $e")
                                }
                            },
                            enabled = isDateValid,
                            shape = CircleShape,
                            modifier = Modifier.size(ButtonDefaults.DefaultButtonSize),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.secondary,
                                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Confirm Birthday",
                                modifier = Modifier.size(ButtonDefaults.DefaultIconSize)
                            )
                        } // End Button

                        // Optional: Remove or keep this text
                        // Text(
                        //     text = "DatePicker", ...
                        // )
                    } // End Column
                } // End Box
            } // End Scaffold
        } // End Outer Box
    } // End if (!showDialog)
} // End Composable function