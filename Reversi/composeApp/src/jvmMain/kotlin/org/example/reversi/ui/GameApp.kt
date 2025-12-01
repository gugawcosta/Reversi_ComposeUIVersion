package org.example.reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import reversi.core.ReversiAction
import kotlin.system.exitProcess

import reversi.model.ReversiBoard
import reversi.core.ReversiState
import reversi.core.reversiGetLegalMoves
import reversi.core.reversiApplyAction
import reversi.core.reversiIsOver

@Composable
@Preview
fun GameApp(viewModel: GameViewModel = remember { GameViewModel() }) {
    // Executa sempre que o estado muda (inclui inicialização e após startNewGame)
    LaunchedEffect(viewModel.currentState) {
        handleNoMovesAndMaybeEnd(
            state = viewModel.currentState,
            board = viewModel.game.board,
            onStateUpdated = { updated -> viewModel.updateState(updated) },
            onGameOver = { final -> viewModel.onGameOver(final) }
        )
    }

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

private fun handleNoMovesAndMaybeEnd(
    state: ReversiState,
    board: ReversiBoard,
    onStateUpdated: (ReversiState) -> Unit,
    onGameOver: (ReversiState) -> Unit
): ReversiState {
    val legal = state.reversiGetLegalMoves(board)
    if (legal.isNotEmpty()) return state

    val passAction = ReversiAction(position = null)
    val nextState = state.reversiApplyAction(passAction, board)
    onStateUpdated(nextState)

    val opponentLegal = nextState.reversiGetLegalMoves(board)
    if (opponentLegal.isEmpty() || nextState.reversiIsOver(board)) {
        onGameOver(nextState)
    }

    return nextState
}

