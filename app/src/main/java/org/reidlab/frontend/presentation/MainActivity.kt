/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package org.reidlab.frontend.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var accepted by remember { mutableStateOf(false) }

            if (accepted) {
                MainAppScreen()
            } else {
                LicenseAgreementScreen(
                    onAccept = { accepted = true },
                    onDecline = { finish() }
                )
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    androidx.wear.compose.material.Scaffold {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.wear.compose.material.Text("Welcome to the App!")
        }
    }
}
