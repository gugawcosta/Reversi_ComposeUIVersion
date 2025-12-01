package reversi.core

import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import reversi.framework.AdversarialBoardGame
import reversi.framework.Cell
import java.io.Serializable
import kotlin.getValue
import kotlin.lazy

/**
 * Represents an immutable snapshot of a Reversi game at a specific point in time.
 *
 * This class implements [AdversarialBoardGame.State] and contains all information
 * necessary to describe the current game:
 * - The positions of all pieces on the board
 * - The current player's turn
 * - Consecutive passes count
 * - The board layout
 * - Computed properties such as legal moves, game-over status, and score
 *
 * All game changes are performed by combining a [ReversiState] with a [ReversiAction]
 * to produce a new [ReversiState], preserving immutability.
 *
 * @property pieces a map of [Cell] to [ReversiPiece], representing the current pieces on the board.
 * @property currentTurn the [ReversiColor] of the player whose turn it is.
 * @property consecutivePasses the number of consecutive passes that have occurred.
 * @property board the [ReversiBoard] representing the layout and dimensions.
 * @property legalMoves the set of legal [ReversiAction]s for the current turn (computed lazily).
 * @property isOver `true` if the game has ended, otherwise `false` (computed lazily).
 * @property score a map of [ReversiColor] to integer score (computed lazily from [pieces]).
 */
data class ReversiState(
    override val pieces: Map<Cell, ReversiPiece>,
    override val currentTurn: ReversiColor,
    val consecutivePasses: Int,
    override val board: ReversiBoard,
):  AdversarialBoardGame.State<ReversiColor, Map<Cell, ReversiPiece>, ReversiAction>,
    Serializable
{

    /** The set of legal moves for the current player, computed lazily. */
    @delegate:Transient
    override val legalMoves by lazy { reversiGetLegalMoves(board) }

    /** Whether the game is over, computed lazily. */
    @delegate:Transient
    override val isOver by lazy { reversiIsOver(board) }

    /** The current score of the game, computed lazily from [pieces]. */
    @delegate:Transient
    val score by lazy { pieces.getScore() }
}