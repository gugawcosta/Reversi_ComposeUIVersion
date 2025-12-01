package reversi.model

import reversi.framework.Cell
import java.io.Serializable

/**
 * Represents a single Reversi piece on the board.
 *
 * Each piece has a [position] on the board and a [color] indicating
 * which player/team it belongs to (BLACK or WHITE).
 *
 * @property position the [Cell] representing the piece's location on the board.
 * @property color the [ReversiColor] of the piece, indicating its owner.
 */
data class ReversiPiece(
    val position: Cell,
    val color: ReversiColor
): Serializable
