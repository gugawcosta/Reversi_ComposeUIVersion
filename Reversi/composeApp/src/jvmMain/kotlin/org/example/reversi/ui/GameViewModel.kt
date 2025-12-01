package org.example.reversi.ui

import androidx.compose.runtime.*
import reversi.core.Reversi
import reversi.core.ReversiState
import reversi.core.reversiGetLegalMoves
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
        get() = currentState.pieces.values.count { it.color == ReversiColor.BLACK }

    val whiteCount: Int
        get() = currentState.pieces.values.count { it.color == ReversiColor.WHITE }

    val gameOver : Boolean
        get() = game.isOver()

    var showTargets by mutableStateOf(false)
        private set

    fun toggleTargets() {
        showTargets = !showTargets
    }

    fun onCellClick(cell: Cell) {
        val action = currentState.reversiGetLegalMoves(game.board)
            .find { it.position == cell } ?: return

        // Aplica a jogada
        game.currentState = game.applyAction(action)
        currentState = game.currentState
    }

    fun getPieceAt(cell: Cell): ReversiPiece? {
        return currentState.pieces[cell]
    }

    // fun getCurrentPlayer(): ReversiColor = currentState.currentTurn

    fun startNewGame() {
        game.reset()
        currentState = game.currentState
    }

    fun updateState(updated: ReversiState) {
        currentState = updated
    }

    fun onGameOver(final: ReversiState) {}
    // temporarily

    fun forcePass() {
        val action = currentState.reversiGetLegalMoves(game.board)
            .find { it.position == null }

        if (action != null) {
            // existe ação de passar: aplica-a normalmente
            game.currentState = game.applyAction(action)
            currentState = game.currentState
            return
        }

        // fallback: força a troca de turno diretamente (se possível)
        try {
            val other = if (currentState.currentTurn == ReversiColor.BLACK) ReversiColor.WHITE else ReversiColor.BLACK
            val newState = currentState.copy(currentTurn = other)
            game.currentState = newState
            currentState = newState
        } catch (e: Throwable) {
            // Se não for possível (p.ex. copy não existe), ignora silenciosamente
        }
    }
}

