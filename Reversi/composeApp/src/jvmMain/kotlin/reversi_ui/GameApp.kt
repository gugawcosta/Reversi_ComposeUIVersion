package reversi_ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import reversi.core.Reversi
import reversi.model.ReversiColor

/**
 * Composable principal do jogo Reversi.
 * @param game O jogo Reversi carregado (pode ser local ou multiplayer).
 * @param gameName O nome do jogo (vazio se local).
 * @param playerColor A cor do jogador local.
 * @param onExit Função chamada quando o utilizador decide sair do jogo.
 */

@Composable
fun GameApp(
    game: Reversi,
    gameName: String,
    playerColor: ReversiColor,
    onExit: () -> Unit
) {
    // 1. Criar ViewModel
    // Não precisamos de .apply { autoRefreshEnabled = true } porque o ViewModel
    // já decide isso sozinho com base no isMultiplayer.
    val viewModel = remember(game) {
        GameViewModel(game, gameName, playerColor)
    }

    // 2. Loop de Auto-Refresh (Otimizado)
    // Usamos 'Unit' para iniciar apenas uma vez e manter o loop vivo.
    LaunchedEffect(Unit) {
        while (true) {
            // Só faz refresh se:
            // 1. For Multiplayer
            // 2. O Refresh estiver ligado
            // 3. Não for a minha vez (eu não preciso de fazer refresh quando é a minha vez)
            // 4. O jogo não acabou
            if (viewModel.isMultiplayer &&
                viewModel.autoRefreshEnabled &&
                viewModel.currentPlayer != viewModel.localPlayerColor &&
                !viewModel.gameOver
            ) {
                viewModel.refreshGame()
            }
            delay(500) // Verifica a cada meio segundo
        }
    }

    // 3. Guardar Backup ao Sair
    DisposableEffect(Unit) {
        onDispose {
            println("A sair do jogo... A guardar estado localmente.")
            viewModel.saveLocalBackup()
        }
    }

    /**
     * UI do Jogo
     * - MenuBar no topo
     * - BoardView e InfoPanel lado a lado
     */

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFCBF4AD).copy(alpha = 0.85f)) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuBar(
                        onNewGame = { /* opcional */ },
                        onJoinGame = { /* opcional */ },
                        onRefresh = { viewModel.refreshGame() },
                        onExit = onExit,
                        onPass = { viewModel.passTurn() },
                        // Passar vez lógica:
                        canPass = if (viewModel.isMultiplayer) {
                            (viewModel.currentPlayer == viewModel.localPlayerColor)
                        } else {
                            true // No singleplayer posso passar a vez de qualquer um
                        },
                        onShowTargetsToggle = { viewModel.toggleTargets() },
                        isShowTargetsOn = viewModel.showTargets,
                        onAutoRefreshToggle = { viewModel.toggleAutoRefresh() },
                        isAutoRefreshOn = viewModel.autoRefreshEnabled,
                        // Botão refresh manual só ativo se multiplayer e desligado o auto-refresh
                        canRefresh = viewModel.isMultiplayer && !viewModel.autoRefreshEnabled
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        BoardView(viewModel)
                        Spacer(modifier = Modifier.width(24.dp))
                        InfoPanel(viewModel)
                    }
                }
            }
        }
    }
}