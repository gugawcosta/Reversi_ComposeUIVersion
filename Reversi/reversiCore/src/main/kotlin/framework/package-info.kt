/**
 * The `reversi.framework` package provides reusable abstractions for turn-based,
 * adversarial board games with teams or players.
 *
 * <p>This package is designed to be fully generic and modular, supporting games
 * like Reversi, Chess, Checkers, Tic-Tac-Toe, and other similar board games.
 * It defines the core interfaces and data structures required to implement
 * any adversarial game logic.</p>
 *
 * <ul>
 *   <li><b>AdversarialBoardGame</b>: Generic interface representing the structure
 *       of a turn-based, adversarial board game, including states, actions, and rules.</li>
 *   <li><b>State</b>: Captures a snapshot of the game at a given time, including
 *       the current turn, pieces, legal moves, and the board.</li>
 *   <li><b>Action</b>: Marker interface representing a move or action in the game.</li>
 *   <li><b>Board</b>: Defines basic properties and operations of a board, including
 *       dimensions and position validation.</li>
 *   <li><b>Team</b>: Represents a team or player with a unique identifier.</li>
 *   <li><b>Cell</b>: Represents a single position on a board.</li>
 *   <li><b>GameResult</b>: Marker interface representing the outcome of a game.</li>
 * </ul>
 *
 * <p>All interfaces in this package are designed to be **highly reusable and
 * extensible**, enabling developers to implement multiple games without
 * modifying the core abstractions.</p>
 *
 * @see AdversarialBoardGame
 * @see Board
 * @see Team
 * @see Cell
 * @see GameResult
 */
package reversi.framework
