package reversi.core

import reversi.framework.AdversarialBoardGame
import reversi.framework.Cell

/**
 * Represents an action (move) in a Reversi game.
 *
 * An action consists of an optional [position] on the board:
 * - If [position] is not null, the action places a piece at the specified cell.
 * - If [position] is null, the action represents a "pass" when no legal moves are available.
 *
 * This class implements [AdversarialBoardGame.Action], allowing it to
 * integrate with generic game frameworks.
 *
 * @property position the [Cell] where the piece will be placed, or `null` to indicate a pass.
 */
data class ReversiAction(val position: Cell? = null) : AdversarialBoardGame.Action