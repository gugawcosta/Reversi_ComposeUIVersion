package reversi.framework

/**
 * Represents a generic game board for turn-based, adversarial games.
 *
 * This interface defines the basic properties and operations that any
 * game board should provide, including its dimensions and validation
 * of positions. It is designed to be reusable across different board
 * games such as Reversi, Chess, Checkers, and Tic-Tac-Toe.
 */
interface Board {

    /** The number of rows in the board. */
    val height: Int

    /** The number of columns in the board. */
    val width: Int

    /**
     * Determines whether a given position is valid on this board.
     *
     * @param pos the [Cell] representing the position to validate.
     * @return `true` if the position is within the bounds of the board, `false` otherwise.
     */
    fun isValid(pos: Cell): Boolean
}
