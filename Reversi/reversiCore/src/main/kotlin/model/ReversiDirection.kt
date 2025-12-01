package reversi.model

import reversi.framework.Cell

/**
 * Represents the eight possible directions on a Reversi board.
 *
 * Each direction is defined by its horizontal (`dx`) and vertical (`dy`) offsets,
 * which can be applied to a [Cell] to traverse the board. This is useful for
 * move validation, flipping pieces, and exploring lines from a given position.
 *
 * Directions follow standard Cartesian offsets:
 * - `dx`: change along the columns (positive = right, negative = left)
 * - `dy`: change along the rows (positive = down, negative = up)
 *
 * Example usage: to move one step diagonally upright, add `(dx, dy)` to the current cell.
 *
 * @property dx the change in column for this direction.
 * @property dy the change in row for this direction.
 */
enum class ReversiDirection(val dx: Int, val dy: Int) {

    /** Upward movement along the board. */
    UP(0, -1),

    /** Downward movement along the board. */
    DOWN(0, 1),

    /** Leftward movement along the board. */
    LEFT(-1, 0),

    /** Rightward movement along the board. */
    RIGHT(1, 0),

    /** Diagonal movement up and to the left. */
    UP_LEFT(-1, -1),

    /** Diagonal movement up and to the right. */
    UP_RIGHT(1, -1),

    /** Diagonal movement down and to the left. */
    DOWN_LEFT(-1, 1),

    /** Diagonal movement down and to the right. */
    DOWN_RIGHT(1, 1)
}
