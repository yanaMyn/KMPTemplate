package org.kmptemplate.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmptemplate.sharedui.generated.resources.Res
import kmptemplate.sharedui.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.kmptemplate.project.walkthrough.WalkthroughScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showWalkthrough by remember { mutableStateOf(false) }
        var showContent by remember { mutableStateOf(false) }

        if (showWalkthrough) {
            WalkthroughScreen(
                onFinished = { showWalkthrough = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(onClick = { showWalkthrough = true }) {
                    Text("Buka Fitur Walkthrough")
                }

                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}