package reversi.model

import reversi.framework.Board
import reversi.framework.Cell
import java.io.Serializable

/**
 * Represents the game board for Reversi.
 *
 * This class implements the generic [Board] interface from the framework
 * and provides Reversi-specific logic, including board dimensions,
 * cell validation, and the initial placement of pieces.
 *
 * @property height the number of rows in the board.
 * @property width the number of columns in the board.
 */
data class ReversiBoard(
    override val height: Int,
    override val width: Int
):  Board,
    Serializable
{

    /** The total number of cells on the board (height * width). */
    val totalCells = height * width

    /**
     * Returns the initial configuration of pieces for a new Reversi game.
     *
     * The standard Reversi starting position is set at the center of the board:
     * - Two white pieces on one diagonal
     * - Two black pieces on the opposite diagonal
     *
     * @return a [Map] associating each [Cell] with its initial [ReversiPiece].
     */
    fun getInitialPieces(): Map<Cell, ReversiPiece> {
        val midWidth1 = width / 2 + 1
        val midWidth2 = width / 2
        val midHeight1 = height / 2 + 1
        val midHeight2 = height / 2

        val pos1 = Cell(midHeight1, midWidth1)
        val pos2 = Cell(midHeight1, midWidth2)
        val pos3 = Cell(midHeight2, midWidth1)
        val pos4 = Cell(midHeight2, midWidth2)

        val initialPieces = mapOf(
            pos1 to ReversiPiece(pos1, ReversiColor.WHITE),
            pos2 to ReversiPiece(pos2, ReversiColor.BLACK),
            pos3 to ReversiPiece(pos3, ReversiColor.BLACK),
            pos4 to ReversiPiece(pos4, ReversiColor.WHITE),
        )

        return initialPieces
    }

    /**
     * Checks whether a given [Cell] position is within the bounds of the board.
     *
     * @param pos the [Cell] to validate.
     * @return `true` if the position is valid (inside the board), `false` otherwise.
     */
    override fun isValid(pos: Cell): Boolean =
        (pos.row in 1..height) && (pos.col in 1..width)
}
