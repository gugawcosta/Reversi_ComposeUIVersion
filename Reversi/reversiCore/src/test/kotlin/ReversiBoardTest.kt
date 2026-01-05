/**
 * ReversiBoard Unit Tests
 *
 * Validates the behavior of the ReversiBoard class from the reversi.model package.
 *
 * Tested functionality:
 * - isValid(Cell): Checks that positions within the board bounds are valid,
 *   and positions outside the board are invalid.
 * - getInitialPieces(): Verifies that the correct starting pieces are placed
 *   for Reversi boards of sizes 4x4, 8x8, and 16x16.
 *
 * Usage:
 * Run this test suite with a Kotlin test runner (e.g., Kotlin Test or JUnit)
 * to confirm correct board initialization and validation logic.
 *
 * @see ReversiBoard
 * @see Cell
 * @see ReversiPiece
 * @see ReversiColor
 */
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReversiBoardTest {
    @Test
    fun testValidPositions() {
        val board = ReversiBoard(8, 8)

        for (row in 1..8)
            for (col in 1..8)
                assertTrue(board.isValid(Cell(row, col)))

        assertFalse(board.isValid(Cell(500000, 4)))
        assertFalse(board.isValid(Cell(4, 500000)))

        assertFalse(board.isValid(Cell(9, 9)))
        assertFalse(board.isValid(Cell(0, 0)))
        assertFalse(board.isValid(Cell(1, 9)))
        assertFalse(board.isValid(Cell(9, 1)))
    }

    @Test
    fun testGetInitialPieces8x8() {
        val board = ReversiBoard(8, 8)
        val pieces = board.getInitialPieces()

        val pos1 = Cell(5, 5)
        val pos2 = Cell(5, 4)
        val pos3 = Cell(4, 5)
        val pos4 = Cell(4, 4)

        assertEquals(4, pieces.size)
        assertEquals(ReversiPiece(pos1, ReversiColor.WHITE), pieces[pos1])
        assertEquals(ReversiPiece(pos2, ReversiColor.BLACK), pieces[pos2])
        assertEquals(ReversiPiece(pos3, ReversiColor.BLACK), pieces[pos3])
        assertEquals(ReversiPiece(pos4, ReversiColor.WHITE), pieces[pos4])
    }

    @Test
    fun testGetInitialPieces4x4() {
        val board = ReversiBoard(4, 4)
        val pieces = board.getInitialPieces()

        val pos1 = Cell(3, 3)
        val pos2 = Cell(3, 2)
        val pos3 = Cell(2, 3)
        val pos4 = Cell(2, 2)

        assertEquals(4, pieces.size)
        assertEquals(ReversiPiece(pos1, ReversiColor.WHITE), pieces[pos1])
        assertEquals(ReversiPiece(pos2, ReversiColor.BLACK), pieces[pos2])
        assertEquals(ReversiPiece(pos3, ReversiColor.BLACK), pieces[pos3])
        assertEquals(ReversiPiece(pos4, ReversiColor.WHITE), pieces[pos4])
    }

    @Test
    fun testGetInitialPieces16x16() {
        val board = ReversiBoard(16, 16)
        val pieces = board.getInitialPieces()

        val pos1 = Cell(9, 9)
        val pos2 = Cell(9, 8)
        val pos3 = Cell(8, 9)
        val pos4 = Cell(8, 8)

        assertEquals(4, pieces.size)
        assertEquals(ReversiPiece(pos1, ReversiColor.WHITE), pieces[pos1])
        assertEquals(ReversiPiece(pos2, ReversiColor.BLACK), pieces[pos2])
        assertEquals(ReversiPiece(pos3, ReversiColor.BLACK), pieces[pos3])
        assertEquals(ReversiPiece(pos4, ReversiColor.WHITE), pieces[pos4])
    }
}