package reversi.core

import reversi.framework.AdversarialBoardGame
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece

/**
 * Default board height for Reversi.
 */
const val BOARD_HEIGHT = 8

/**
 * Default board width for Reversi.
 */
const val BOARD_WIDTH = 8

/**
 * Minimum allowed board height.
 */
const val MINIMUM_BOARD_HEIGHT = 4

/**
 * Minimum allowed board width.
 */
const val MINIMUM_BOARD_WIDTH = 4

/**
 * Maximum allowed board height.
 */
const val MAXIMUM_BOARD_HEIGHT = 16

/**
 * Maximum allowed board width.
 */
const val MAXIMUM_BOARD_WIDTH = 16

/**
 * Maximum consecutive passes allowed before the game ends.
 */
const val MAXIMUM_CONSECUTIVE_PASSES = 2

/**
 * Default initialized board.
 */
val BOARD = ReversiBoard(BOARD_HEIGHT, BOARD_WIDTH)

/**
 * Main Reversi game controller implementing [AdversarialBoardGame].
 *
 * This class manages the current game state, enforces rules, and applies actions.
 * The state is immutable and the only point of mutation is [currentState].
 *
 * @property board the Reversi board associated with this game.
 * @property startingColor the color of the player who starts the game (default: [ReversiColor.BLACK]).
 */
class Reversi(
    override val board: ReversiBoard = BOARD,
    val startingColor: ReversiColor = ReversiColor.BLACK
) : AdversarialBoardGame<
        ReversiColor,
        Map<Cell, ReversiPiece>,
        ReversiState,
        ReversiAction
        > {

    /**
     * The current immutable game state.
     */
    override var currentState: ReversiState = initialSetup()

    init {
        require(board.height % 2 == 0) { "Board height must be even" }
        require(board.width % 2 == 0) { "Board width must be even" }
        require(board.height == board.width) { "Board must be a square" }
        require(board.height in MINIMUM_BOARD_HEIGHT..MAXIMUM_BOARD_HEIGHT) {
            "Board height must be between $MINIMUM_BOARD_HEIGHT and $MAXIMUM_BOARD_HEIGHT"
        }
        require(board.width in MINIMUM_BOARD_WIDTH..MAXIMUM_BOARD_WIDTH) {
            "Board width must be between $MINIMUM_BOARD_WIDTH and $MAXIMUM_BOARD_WIDTH"
        }
    }

    /**
     * Initializes a new game state with the starting pieces and starting color.
     *
     * @return a new [ReversiState] representing the starting state of the game.
     */
    override fun initialSetup(): ReversiState {
        val initialPieces = board.getInitialPieces()

        return ReversiState(
            pieces = initialPieces,
            currentTurn = startingColor,
            consecutivePasses = 0,
            board = board
        )
    }
}
