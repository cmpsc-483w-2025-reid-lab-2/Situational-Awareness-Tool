package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import org.reidlab.frontend.R


// Custom Wear OS Colors
private val CustomWearColors = Colors(
    primary = Color(0xFFC1E1C1), // Light Green (Accept Button)
    onPrimary = Color.Black,       // Black (Checkmark on Accept)
    secondary = Color(0xFFCF6679), //  Red (Decline Button)
    onSecondary = Color.White,     // White (X on Decline)
    background = Color.Black,      // Black (App Background)
    onBackground = Color.White,     // White (Text on Background)
    surface = Color(0xFF1E1E1E),  // Dark Grey (for Surfaces)
    onSurface = Color.White,       // White (Text on Surfaces)
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun CustomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = CustomWearColors,
        // You can also customize typography and shapes here
        content = content
    )
}

@Composable
fun AcceptDeclineButtons(onAccept: () -> Unit, onDecline: () -> Unit) {
    // Wrap the Row in a Box to center it
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            // modifier = Modifier.fillMaxWidth(), // Removed fillMaxWidth from Row
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decline Button
            Button(
                onClick = onDecline,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary
                ),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Decline",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }

            // Accept Button
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Accept",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun LicenseAgreementScreen(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    headerTextSize: TextUnit = 16.5.sp, // Default header size
    bodyTextSize: TextUnit = 13.sp    // Default body size
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.eula_header),
            style = TextStyle( // Use TextStyle for full control
                fontSize = headerTextSize,
                fontFamily = MaterialTheme.typography.title2.fontFamily, // Keep font family
                fontWeight = MaterialTheme.typography.title2.fontWeight,   // Keep font weight
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier.padding(bottom = 8.dp, top = 25.dp)
        )

        Text(
            text = stringResource(R.string.eula_body),
            style = TextStyle( // Use TextStyle
                fontSize = bodyTextSize,
                fontFamily = MaterialTheme.typography.body2.fontFamily,
                fontWeight = MaterialTheme.typography.body2.fontWeight,
                textAlign = TextAlign.Left, // Or Justify, as you prefer
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AcceptDeclineButtons(onAccept = onAccept, onDecline = onDecline)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Previews
@Composable
fun PreviewAcceptDeclineButtons() {
    CustomTheme {
        AcceptDeclineButtons(onAccept = {}, onDecline = {})
    }
}

@Composable
fun PreviewLicenseAgreement() {
    CustomTheme {
        LicenseAgreementScreen(onAccept = {}, onDecline = {})
    }
}