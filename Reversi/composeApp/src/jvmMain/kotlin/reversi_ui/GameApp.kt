package reversi_ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.system.exitProcess

@Composable
@Preview
fun GameApp(viewModel: GameViewModel = remember { GameViewModel() }) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFCBF4AD).copy(alpha = 0.85f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuBar(
                        onNewGame = {
                            // Apenas inicia novo jogo; o LaunchedEffect acima reage ao novo estado
                            viewModel.startNewGame()
                        },
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