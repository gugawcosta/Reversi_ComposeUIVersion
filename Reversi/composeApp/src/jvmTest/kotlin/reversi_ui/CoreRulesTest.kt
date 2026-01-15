package reversi.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reversi.core.Reversi
import reversi.core.ReversiAction
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor

class CoreRulesTest {

    @Test
    fun `initial board must have correct dimensions`() {
        val board = ReversiBoard(8, 8)
        assertEquals(8, board.height)
        assertEquals(8, board.width)
        assertEquals(64, board.totalCells)
    }

    @Test
    fun `game starts with exactly 4 pieces`() {
        val game = Reversi(ReversiBoard(8, 8))
        assertEquals(4, game.currentState.pieces.size, "O jogo deve começar com 4 peças no total")
    }

    @Test
    fun `initial pieces are placed correctly in center`() {
        val game = Reversi(ReversiBoard(8, 8))
        val pieces = game.currentState.pieces

        // Padrão Reversi:
        // (4,4)=Branca, (5,5)=Branca
        // (4,5)=Preta, (5,4)=Preta
        assertEquals(ReversiColor.WHITE, pieces[Cell(4, 4)]?.color)
        assertEquals(ReversiColor.WHITE, pieces[Cell(5, 5)]?.color)
        assertEquals(ReversiColor.BLACK, pieces[Cell(4, 5)]?.color)
        assertEquals(ReversiColor.BLACK, pieces[Cell(5, 4)]?.color)
    }

    @Test
    fun `black player must play first`() {
        val game = Reversi(ReversiBoard(8, 8))
        assertEquals(ReversiColor.BLACK, game.currentState.currentTurn)
    }

    @Test
    fun `initial score must be 2-2`() {
        val game = Reversi(ReversiBoard(8, 8))
        assertEquals(2, game.currentState.score.black)
        assertEquals(2, game.currentState.score.white)
    }

    @Test
    fun `cells inside boundaries are valid`() {
        val board = ReversiBoard(8, 8)
        assertTrue(board.isValid(Cell(1, 1)))
        assertTrue(board.isValid(Cell(8, 8)))
        assertTrue(board.isValid(Cell(4, 5)))
    }

    @Test
    fun `cells outside boundaries are invalid`() {
        val board = ReversiBoard(8, 8)
        assertFalse(board.isValid(Cell(0, 0)))
        assertFalse(board.isValid(Cell(9, 1)))
        assertFalse(board.isValid(Cell(1, 9)))
        assertFalse(board.isValid(Cell(-1, 5)))
    }

    @Test
    fun `initial state has legal moves for black`() {
        val game = Reversi(ReversiBoard(8, 8))
        val legalMoves = game.currentState.legalMoves

        assertFalse(legalMoves.isEmpty(), "As pretas devem ter jogadas disponíveis no início")
        // Verifica jogadas clássicas: (3,4), (4,3), (5,6), (6,5)
        assertTrue(legalMoves.any { it.position == Cell(3, 4) })
        assertTrue(legalMoves.any { it.position == Cell(5, 6) })
    }

    @Test
    fun `making a move changes turn to white`() {
        val game = Reversi(ReversiBoard(8, 8))
        val move = ReversiAction(Cell(3, 4)) // Jogada válida para pretas

        val newState = game.currentState.applyAction(move)

        assertEquals(ReversiColor.WHITE, newState.currentTurn, "Após jogada das pretas, deve ser vez das brancas")
    }

    @Test
    fun `new game is not over`() {
        val game = Reversi(ReversiBoard(8, 8))
        assertFalse(game.currentState.isOver, "O jogo não deve começar no estado 'Game Over'")
    }
}