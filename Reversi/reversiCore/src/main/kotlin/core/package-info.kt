/**
 * Core logic and game engine for Reversi.
 *
 * <p>
 * This package provides the primary game mechanics for Reversi, including the mainApp.main controller,
 * immutable game states, actions, scoring, results, and rule-processing logic.
 * It integrates with the generic [reversi.framework.AdversarialBoardGame] interface
 * to support reusable, turn-based game mechanics.
 * </p>
 *
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link Reversi} – Main game controller that holds the current state and enforces rules.</li>
 *   <li>{@link ReversiState} – Immutable snapshot of the game at a specific point in time.</li>
 *   <li>{@link ReversiAction} – Represents a player move or a pass.</li>
 *   <li>{@link ReversiScore} – Holds the current piece counts for both players.</li>
 *   <li>{@link ReversiResult} – Enum representing the outcome of a game.</li>
 * </ul>
 *
 * <h2>Key Functions</h2>
 * <ul>
 *   <li>{@link ReversiState.reversiApplyAction} – Applies an action to a state, producing a new state.</li>
 *   <li>{@link ReversiState.reversiGetLegalMoves} – Returns all legal moves for the current player.</li>
 *   <li>{@link ReversiState.reversiIsOver} – Determines if the game has ended.</li>
 *   <li>{@link ReversiState.reversiGetResult} – Returns the result of the game (win, draw, ongoing).</li>
 *   <li>{@link Map.getScore} – Computes the current score from the board mapping.</li>
 * </ul>
 *
 * <h2>Constants</h2>
 * <ul>
 *   <li>{@link BOARD_HEIGHT}, {@link BOARD_WIDTH} – Default board dimensions.</li>
 *   <li>{@link MINIMUM_BOARD_HEIGHT}, {@link MINIMUM_BOARD_WIDTH}, {@link MAXIMUM_BOARD_HEIGHT}, {@link MAXIMUM_BOARD_WIDTH} – Board size limits.</li>
 *   <li>{@link MAXIMUM_CONSECUTIVE_PASSES} – Maximum consecutive passes before the game ends.</li>
 *   <li>{@link BOARD} – Default initialized board.</li>
 * </ul>
 *
 * <p>
 * This package is designed to be immutable, reusable, and easily integrated with
 * any user interface (such as a command-prompt UI) or automated testing.
 * </p>
 *
 * @see Reversi
 * @see ReversiState
 * @see ReversiAction
 * @see ReversiScore
 * @see ReversiResult
 */
package reversi.core
