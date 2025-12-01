package reversi.core

import reversi.framework.GameResult

/**
 * Represents the possible outcomes of a Reversi game.
 *
 * This enum implements [GameResult] and is used to indicate the
 * current or final result of a game.
 *
 * @property BLACK_WINS indicates that the black player has won.
 * @property WHITE_WINS indicates that the white player has won.
 * @property DRAW indicates the game ended in a tie.
 * @property ONGOING indicates that the game is still in progress.
 */
enum class ReversiResult : GameResult {
    /** Black player has won the game. */
    BLACK_WINS,

    /** White player has won the game. */
    WHITE_WINS,

    /** The game ended in a tie. */
    DRAW,

    /** The game is still ongoing. */
    ONGOING
}
