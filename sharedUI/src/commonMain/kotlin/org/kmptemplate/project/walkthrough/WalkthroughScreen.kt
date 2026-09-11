package org.kmptemplate.project.walkthrough

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kmptemplate.project.walkthrough.model.WalkthroughItem
import org.kmptemplate.project.walkthrough.mvi.WalkthroughIntent
import org.kmptemplate.project.walkthrough.mvi.WalkthroughState
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

@Composable
fun WalkthroughScreen(
    store: WalkthroughStore = remember { WalkthroughStore() },
    onFinished: () -> Unit = {}
) {
    val state by store.state.collectAsState()

    if (state.isCompleted) {
        WalkthroughCompletedView(
            onRestart = { store.dispatch(WalkthroughIntent.LoadItems) },
            onContinue = onFinished
        )
    } else {
        WalkthroughContentView(
            state = state,
            onIntent = { store.dispatch(it) }
        )
    }
}

@Composable
private fun WalkthroughContentView(
    state: WalkthroughState,
    onIntent: (WalkthroughIntent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!state.isLastPage) {
                    TextButton(
                        onClick = { onIntent(WalkthroughIntent.Skip) }
                    ) {
                        Text(
                            text = "Lewati",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Center: Slide Content
            AnimatedContent(
                targetState = state.currentItem,
                label = "WalkthroughSlide",
                modifier = Modifier.weight(1f)
            ) { item ->
                if (item != null) {
                    SlideCard(item = item)
                }
            }

            // Footer: Indicators & Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                PageIndicators(
                    totalPages = state.totalPages,
                    currentIndex = state.currentIndex,
                    onSelectPage = { onIntent(WalkthroughIntent.SelectPage(it)) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!state.isFirstPage) {
                        OutlinedButton(
                            onClick = { onIntent(WalkthroughIntent.PreviousPage) },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Sebelumnya")
                        }
                    }

                    Button(
                        onClick = { onIntent(WalkthroughIntent.NextPage) },
                        modifier = Modifier
                            .weight(if (state.isFirstPage) 1f else 1.2f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (state.isLastPage) "Mulai Sekarang" else "Lanjut",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlideCard(item: WalkthroughItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon / Graphic Placeholder
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (item.iconName) {
                    "sparkles" -> "✨"
                    "layers" -> "🧱"
                    "devices" -> "📱"
                    else -> "🚀"
                },
                fontSize = 54.sp
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PageIndicators(
    totalPages: Int,
    currentIndex: Int,
    onSelectPage: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalPages) {
            val isSelected = i == currentIndex
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isSelected) 28.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
                    .clickable { onSelectPage(i) }
            )
        }
    }
}

@Composable
private fun WalkthroughCompletedView(
    onRestart: () -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎉", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Siap Digunakan!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Anda telah menyelesaikan panduan awal. Selamat menjelajah aplikasi!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Masuk ke Beranda", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ulangi Panduan")
            }
        }
    }
}
