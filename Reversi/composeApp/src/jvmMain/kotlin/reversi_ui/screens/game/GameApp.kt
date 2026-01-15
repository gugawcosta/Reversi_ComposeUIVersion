package reversi_ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi_ui.components.BoardView
import reversi_ui.components.InfoPanel
import reversi_ui.components.MenuBar
import reversi_viewmodel.GameViewModel

/**
 * Composable principal do jogo Reversi.
 * @param game Instância do jogo Reversi.
 * @param gameName Nome do jogo (para multiplayer).
 * @param playerColor Cor do jogador local.
 * @param onExit Função chamada quando o user decide sair do jogo.
 */
@Composable
fun GameApp(
    game: Reversi,
    gameName: String,
    playerColor: ReversiColor,
    onExit: () -> Unit
) {
    val viewModel = remember(game) {
        GameViewModel(game, gameName, playerColor) // Inicializa o ViewModel com o jogo e a cor do jogador
    }

    // Loop de Auto-Refresh
    LaunchedEffect(Unit) {
        while (true) {
            if (viewModel.isMultiplayer &&
                viewModel.autoRefreshEnabled &&
                viewModel.currentPlayer != viewModel.localPlayerColor &&
                !viewModel.gameOver
            ) {
                viewModel.refreshGame()
            }
            delay(1000)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.saveLocalBackup() }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFCBF4AD).copy(alpha = 0.85f)) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Mudei a estrutura principal para garantir centragem
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // MenuBar no topo
                    MenuBar(
                        onRefresh = { viewModel.refreshGame() },
                        onExit = onExit,
                        onAutoRefreshToggle = { viewModel.toggleAutoRefresh() },
                        isAutoRefreshOn = viewModel.autoRefreshEnabled,
                        canRefresh = viewModel.isMultiplayer && !viewModel.autoRefreshEnabled
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Área de jogo: Usa weight(1f) para ocupar a vertical toda e Center para centrar o tabuleiro
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BoardView(viewModel)
                            Spacer(modifier = Modifier.width(24.dp))
                            InfoPanel(viewModel)
                        }
                    }
                }

                // Overlay de Erro
                if (viewModel.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .background(Color.Red.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}