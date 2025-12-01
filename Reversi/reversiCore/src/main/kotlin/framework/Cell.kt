package reversi.framework

import java.io.Serializable

/**
 * Represents a single position on a game board.
 *
 * A [Cell] is defined by its row and column coordinates. It is used
 * throughout the framework to identify locations of pieces, legal moves,
 * and other board interactions.
 *
 * @property row the row number of the cell (typically 1-based).
 * @property col the column number of the cell (typically 1-based).
 */
data class Cell(
    val row: Int,
    val col: Int,
): Serializable
