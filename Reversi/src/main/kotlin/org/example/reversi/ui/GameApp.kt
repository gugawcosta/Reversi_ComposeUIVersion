package org.example.reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.system.exitProcess

@Composable
@Preview
fun GameApp(viewModel: GameViewModel = remember { GameViewModel() }) {
    MaterialTheme {
        // Set the window background here so it applies to the whole app window.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0B1E2D) // <-- change this color to what you prefer for the window background
        ) {
            // Use a Box so we can position an Image in the top-left corner and keep the app content centered
            Box(modifier = Modifier.fillMaxSize()) {
                // Top-left image: put your PNG at src/main/resources/icon.png

                // Main app content (kept as before), centered/padded
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuBar(
                        onNewGame = { viewModel.startNewGame() },
                        onExit = { exitProcess(0) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BoardView(viewModel)
                        Spacer(modifier = Modifier.width(24.dp))
                        InfoPanel(viewModel)
                    }
                }
            }
        }
    }
}
