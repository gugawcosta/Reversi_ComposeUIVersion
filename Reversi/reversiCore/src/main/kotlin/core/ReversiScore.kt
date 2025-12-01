package reversi.core

/**
 * Represents the score of a Reversi game.
 *
 * This class holds the current number of pieces for each player.
 * It is used by [ReversiState] to report the current game score.
 *
 * @property white the number of white pieces on the board.
 * @property black the number of black pieces on the board.
 */
data class ReversiScore(val white: Int, val black: Int)
