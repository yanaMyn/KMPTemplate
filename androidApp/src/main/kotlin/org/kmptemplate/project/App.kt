package org.kmptemplate.project

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kmptemplate.project.auth.LoginRoute
import org.kmptemplate.project.auth.RegisterRoute
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.walkthrough.WalkthroughRoute

@Composable
fun App() {
    MaterialTheme {
        var showWalkthrough by remember { mutableStateOf(false) }
        var showLogin by remember { mutableStateOf(false) }
        var showRegister by remember { mutableStateOf(false) }
        var loggedInUser by remember { mutableStateOf<User?>(null) }
        var showContent by remember { mutableStateOf(false) }

        if (showWalkthrough) {
            WalkthroughRoute(onFinished = { showWalkthrough = false })
        } else if (showLogin) {
            LoginRoute(
                onLoginSuccess = { user ->
                    loggedInUser = user
                    showLogin = false
                },
                onNavigateToRegister = {
                    showLogin = false
                    showRegister = true
                },
                onBack = { showLogin = false }
            )
        } else if (showRegister) {
            RegisterRoute(
                onRegisterSuccess = { user ->
                    loggedInUser = user
                    showRegister = false
                },
                onNavigateToLogin = {
                    showRegister = false
                    showLogin = true
                },
                onBack = { showRegister = false }
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
                if (loggedInUser != null) {
                    Text(
                        text = "Halo, ${loggedInUser?.name} (${loggedInUser?.email})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { loggedInUser = null }) {
                        Text("Logout")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(onClick = { showContent = !showContent }) {
                    Text(if (showContent) "Hide Greeting" else "Show Greeting")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { showLogin = true }) {
                    Text("Buka Halaman Login")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { showRegister = true }) {
                    Text("Buat Akun Baru (Register)")
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
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}
