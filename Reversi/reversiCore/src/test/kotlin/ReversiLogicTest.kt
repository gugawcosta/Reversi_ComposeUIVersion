/**
 * ReversiLogic Unit Tests
 *
 * Validates the core game logic in the reversi.core package, including state transitions,
 * legal move detection, score calculation, and game termination conditions.
 *
 * Tested functionality:
 * - reversiIsOver(): Checks if the game ends when the board is full or after maximum consecutive passes.
 * - reversiGetResult(): Confirms the correct game result (ongoing, win, draw) based on the board state.
 * - reversiApplyAction(): Validates piece placement, flipping logic, pass handling, and turn switching.
 * - reversiGetLegalMoves(): Ensures legal moves are correctly detected according to Reversi rules.
 * - getScore(): Counts white and black pieces accurately.
 *
 * Usage:
 * Run this test suite with a Kotlin test runner (e.g., Kotlin Test or JUnit)
 * to verify correctness of the Reversi game logic.
 *
 * @see ReversiState
 * @see ReversiAction
 * @see ReversiResult
 * @see getScore
 * @see reversiApplyAction
 * @see reversiGetLegalMoves
 * @see reversiGetResult
 * @see reversiIsOver
 */
import kotlin.test.*
import reversi.core.MAXIMUM_CONSECUTIVE_PASSES
import reversi.core.ReversiAction
import reversi.core.ReversiResult
import reversi.core.ReversiState
import reversi.core.getScore
import reversi.core.reversiApplyAction
import reversi.core.reversiGetLegalMoves
import reversi.core.reversiGetResult
import reversi.core.reversiIsOver
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece

class ReversiLogicTest {

    private val board = ReversiBoard(8, 8)

    @Test
    fun testReversiIsOverWhenBoardIsFull() {
        val fullState = ReversiState(
            pieces = makePieces(32, 32),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = 0,
            board = board
        )
        assertTrue(fullState.reversiIsOver(board), "Game should be over when the board is full")
    }

    @Test
    fun testReversiIsOverWhenMaxConsecutivePassesReached() {
        val partialState = ReversiState(
            pieces = makePieces(10, 8),
            currentTurn = ReversiColor.WHITE,
            consecutivePasses = MAXIMUM_CONSECUTIVE_PASSES,
            board = board
        )
        assertTrue(partialState.reversiIsOver(board), "Game should be over after two consecutive passes")
    }

    @Test
    fun testReversiIsNotOverNormally() {
        val partialState = ReversiState(
            pieces = makePieces(10, 8),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = 0,
            board = board
        )
        assertFalse(partialState.reversiIsOver(board), "Game should not be over yet")
    }

    @Test
    fun testReversiGetResultOngoing() {
        val state = ReversiState(
            pieces = makePieces(10, 8),
            currentTurn = ReversiColor.WHITE,
            consecutivePasses = 0,
            board = board
        )
        assertEquals(ReversiResult.ONGOING, state.reversiGetResult(board))
    }

    @Test
    fun testReversiGetResultWhiteWins() {
        val state = ReversiState(
            pieces = makePieces(40, 24),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = MAXIMUM_CONSECUTIVE_PASSES,
            board = board
        )
        assertEquals(ReversiResult.WHITE_WINS, state.reversiGetResult(board))
    }

    @Test
    fun testReversiGetResultBlackWins() {
        val state = ReversiState(
            pieces = makePieces(20, 44),
            currentTurn = ReversiColor.WHITE,
            consecutivePasses = MAXIMUM_CONSECUTIVE_PASSES,
            board = board
        )
        assertEquals(ReversiResult.BLACK_WINS, state.reversiGetResult(board))
    }

    @Test
    fun testReversiGetResultDraw() {
        val state = ReversiState(
            pieces = makePieces(32, 32),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = MAXIMUM_CONSECUTIVE_PASSES,
            board = board
        )
        assertEquals(ReversiResult.DRAW, state.reversiGetResult(board))
    }

    @Test
    fun testPassActionIncrementsConsecutivePassesAndSwitchesTurn() {
        val state = ReversiState(
            pieces = makePieces(2, 2),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = 0,
            board = board
        )
        val passAction = ReversiAction(null) // Passing turn
        val newState = state.reversiApplyAction(passAction, board)
        assertEquals(ReversiColor.WHITE, newState.currentTurn, "Turn should switch after pass")
        assertEquals(1, newState.consecutivePasses, "Consecutive passes should increment by 1")
        assertEquals(state.pieces, newState.pieces, "Pieces should not change after pass")
    }

    @Test
    fun testPiecePlacementAddsNewPiece() {
        val state = ReversiState(
            pieces = makePieces(2, 2),
            currentTurn = ReversiColor.BLACK,
            consecutivePasses = 0,
            board = board
        )
        val action = ReversiAction(Cell(3, 3))
        val newState = state.reversiApplyAction(action, board)
        assertTrue(newState.pieces.containsKey(Cell(3, 3)), "New piece should be placed on the board")
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(3, 3)]?.color, "Placed piece should match current turn color")
        assertEquals(ReversiColor.WHITE, newState.currentTurn, "Turn should switch after placing a piece")
        assertEquals(0, newState.consecutivePasses, "Consecutive passes should reset after placing a piece")
    }

    @Test
    fun testFlippingPiecesInLine() {
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(4, 1)] = ReversiPiece(Cell(4, 1), ReversiColor.BLACK)
        pieces[Cell(4, 2)] = ReversiPiece(Cell(4, 2), ReversiColor.WHITE)
        pieces[Cell(4, 3)] = ReversiPiece(Cell(4, 3), ReversiColor.WHITE)
        val state = ReversiState(pieces, ReversiColor.BLACK, 0, board)
        val action = ReversiAction(Cell(4, 4))
        val newState = state.reversiApplyAction(action, board)
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(4, 2)]?.color)
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(4, 3)]?.color)
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(4, 4)]?.color)
    }

    @Test
    fun testFlippingPiecesStopsAtOwnColor() {
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(4, 1)] = ReversiPiece(Cell(4, 1), ReversiColor.BLACK)
        pieces[Cell(4, 2)] = ReversiPiece(Cell(4, 2), ReversiColor.WHITE)
        pieces[Cell(4, 3)] = ReversiPiece(Cell(4, 3), ReversiColor.BLACK)
        val state = ReversiState(pieces, ReversiColor.BLACK, 0, board)
        val action = ReversiAction(Cell(4, 4))
        val newState = state.reversiApplyAction(action, board)
        assertEquals(ReversiColor.WHITE, newState.pieces[Cell(4, 2)]?.color)
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(4, 3)]?.color)
        assertEquals(ReversiColor.BLACK, newState.pieces[Cell(4, 4)]?.color)
    }

    @Test
    fun testLegalMovesDetection() {
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(4, 1)] = ReversiPiece(Cell(4, 1), ReversiColor.BLACK)
        pieces[Cell(4, 2)] = ReversiPiece(Cell(4, 2), ReversiColor.WHITE)
        val state = ReversiState(pieces, ReversiColor.BLACK, 0, board)
        val legalMoves = state.reversiGetLegalMoves(board)
        assertTrue(ReversiAction(Cell(4, 3)) in legalMoves)
        assertFalse(ReversiAction(Cell(4, 4)) in legalMoves)
        assertEquals(1, legalMoves.size)
    }

    @Test
    fun testNoLegalMoves() {
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(4, 1)] = ReversiPiece(Cell(4, 1), ReversiColor.BLACK)
        val state = ReversiState(pieces, ReversiColor.BLACK, 0, board)
        val legalMoves = state.reversiGetLegalMoves(board)
        assertTrue(legalMoves.isEmpty())
    }

    @Test
    fun testGetScoreCountsPiecesCorrectly() {
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(1, 1)] = ReversiPiece(Cell(1, 1), ReversiColor.WHITE)
        pieces[Cell(1, 2)] = ReversiPiece(Cell(1, 2), ReversiColor.BLACK)
        pieces[Cell(1, 3)] = ReversiPiece(Cell(1, 3), ReversiColor.WHITE)
        pieces[Cell(2, 1)] = ReversiPiece(Cell(2, 1), ReversiColor.BLACK)
        val score = pieces.getScore()
        assertEquals(2, score.white)
        assertEquals(2, score.black)
    }

    @Test
    fun testGetScoreEmptyBoard() {
        val emptyPieces = emptyMap<Cell, ReversiPiece>()
        val score = emptyPieces.getScore()
        assertEquals(0, score.white)
        assertEquals(0, score.black)
    }
}

private fun makePieces(whiteCount: Int, blackCount: Int): Map<Cell, ReversiPiece> {
    val pieces = mutableMapOf<Cell, ReversiPiece>()
    var index = 0
    repeat(whiteCount) {
        pieces[Cell(index / 8 + 1, index % 8 + 1)] = ReversiPiece(Cell(index / 8 + 1, index % 8 + 1), ReversiColor.WHITE)
        index++
    }
    repeat(blackCount) {
        pieces[Cell(index / 8 + 1, index % 8 + 1)] = ReversiPiece(Cell(index / 8 + 1, index % 8 + 1), ReversiColor.BLACK)
        index++
    }
    return pieces
}
