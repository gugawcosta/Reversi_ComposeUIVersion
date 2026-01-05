/**
 * Reversi Game Unit Tests
 *
 * Tests the Reversi class from the reversi.core package, focusing on
 * proper initial state setup and validation of board constraints.
 *
 * Tested functionality:
 * - setup(): Verifies that the initial game state is created correctly.
 * - Board validation: Ensures exceptions are thrown for invalid board sizes
 *   or non-square boards.
 *
 * Usage:
 * Run this test suite with a Kotlin test runner (e.g., Kotlin Test or JUnit)
 * to validate the correctness of the Reversi game initialization logic.
 *
 * @see Reversi
 * @see ReversiBoard
 * @see ReversiColor
 * @see ReversiPiece
 * @see Cell
 */
import org.junit.jupiter.api.Assertions.assertEquals
import reversi.framework.Cell
import reversi.core.Reversi
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ReversiTest {

    @Test
    fun testSetupCreatesInitialStateCorrectly() {
        val game = Reversi(ReversiBoard(8, 8), ReversiColor.BLACK)
        val state = game.initialSetup()

        assertEquals(ReversiColor.BLACK, state.currentTurn, "Starting color should be BLACK")
        assertEquals(0, state.consecutivePasses, "Consecutive passes should start at 0")

        val pieces = state.pieces
        assertEquals(4, pieces.size, "Initial board should contain 4 pieces")

        val pos1 = Cell(5, 5)
        val pos2 = Cell(5, 4)
        val pos3 = Cell(4, 5)
        val pos4 = Cell(4, 4)

        assertEquals(ReversiPiece(pos1, ReversiColor.WHITE), pieces[pos1])
        assertEquals(ReversiPiece(pos2, ReversiColor.BLACK), pieces[pos2])
        assertEquals(ReversiPiece(pos3, ReversiColor.BLACK), pieces[pos3])
        assertEquals(ReversiPiece(pos4, ReversiColor.WHITE), pieces[pos4])
    }

    @Test
    fun testInvalidBoardHeightThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            Reversi(ReversiBoard(3, 8))
        }
    }

    @Test
    fun testInvalidBoardWidthThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            Reversi(ReversiBoard(8, 5))
        }
    }

    @Test
    fun testNonSquareBoardThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            Reversi(ReversiBoard(8, 6))
        }
    }

    @Test
    fun testBoardTooSmallThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            Reversi(ReversiBoard(2, 2))
        }
    }

    @Test
    fun testBoardTooLargeThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            Reversi(ReversiBoard(20, 20))
        }
    }
}
