package org.reidlab.frontend.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons // Import the Icons object
import androidx.compose.material.icons.filled.Check // Import specific icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown


@Composable
fun LicenseAgreementScreen(onAccept: () -> Unit, onDecline: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "End User License Agreement",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.title3
        )

        Text(
            text = "By using this app, you agree to the following terms...",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.body1
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompactButton(
                onClick = onDecline,
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Text("X") // Still a fallback, but icon is preferred
            }

            Button(
                onClick = onAccept,
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text("✓")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onDecline) {
                Icon(
                    imageVector = Icons.Filled.Close, // Use the built-in Close icon
                    contentDescription = "Decline",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
            Button(onClick = onAccept) {
                Icon(
                    imageVector = Icons.Filled.Check, // Use the built-in Check icon
                    contentDescription = "✓",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        }
        //Optional "show more button"
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Show More",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}