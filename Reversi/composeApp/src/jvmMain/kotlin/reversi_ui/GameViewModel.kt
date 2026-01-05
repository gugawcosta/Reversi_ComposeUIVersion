package reversi_ui

import androidx.compose.runtime.*
import reversi.core.Reversi
import reversi.core.ReversiAction
import reversi.core.ReversiState
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import reversi.framework.Cell

class GameViewModel {
    val currentPlayer: ReversiColor
        get() = currentState.currentTurn
    val game = Reversi()

    // Estado observável para Compose
    var currentState by mutableStateOf(value = game.currentState)
        private set

    val blackCount: Int
        get() = currentState.score.black

    val whiteCount: Int
        get() = currentState.score.white

    val gameOver : Boolean
        get() = currentState.isOver

    var showTargets by mutableStateOf(false)
        private set

    fun toggleTargets() {
        showTargets = !showTargets
    }

    fun onCellClick(cell: Cell) {
        val doneAction = ReversiAction(cell)

        if (doneAction !in currentState.legalMoves)
            return

        // Aplica a jogada
        game.currentState = currentState.applyAction(doneAction)
        currentState = game.currentState
    }

    fun passTurn() {
        println("Pass Triggered. Changing turn from ${currentState.currentTurn}...")

        game.currentState = currentState.applyAction(ReversiAction.PASS)
        currentState = game.currentState

        println("...New turn is now: ${currentState.currentTurn}")
        println("...New player has ${currentState.legalMoves.size} moves.")
    }

    fun getPieceAt(cell: Cell): ReversiPiece? = currentState.pieces[cell]

    fun startNewGame() {
        game.currentState = game.initialSetup()
        currentState = game.currentState
    }

    fun updateState(updated: ReversiState) {
        currentState = updated
    }

    fun onGameOver(final: ReversiState) {

    }
}

