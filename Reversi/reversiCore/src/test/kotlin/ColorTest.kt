/**
 * ReversiColor Unit Tests
 *
 * Tests the behavior of the ReversiColor enum in the reversi.model package.
 *
 * Tested functionality:
 * - invertColor(): Ensures that each color correctly inverts to its opposite.
 *
 * Usage:
 * Run with a Kotlin test runner (e.g., JUnit, Kotlin Test) to verify correctness.
 *
 * @see ReversiColor
 */
import reversi.model.ReversiColor
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun testInvertColor() {
        val color = ReversiColor.WHITE
        val colorInv1 = color.invertColor()
        assertEquals(ReversiColor.BLACK, colorInv1)
        assertEquals(ReversiColor.WHITE, colorInv1.invertColor())
    }
}
