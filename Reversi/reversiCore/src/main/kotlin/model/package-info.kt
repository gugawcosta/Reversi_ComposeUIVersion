/**
 * The `reversi.model` package contains Reversi-specific data structures
 * and types used to represent the board, pieces, colors, and move directions.
 *
 * <p>This package provides the core building blocks for the Reversi game
 * logic in the `core` package, while remaining independent of UI or input handling.</p>
 *
 * <ul>
 *   <li><b>ReversiBoard</b>: Represents the board, its dimensions, and initial piece setup.</li>
 *   <li><b>ReversiPiece</b>: Represents a single piece on the board with a position and color.</li>
 *   <li><b>ReversiColor</b>: Enum representing BLACK or WHITE teams/players.</li>
 *   <li><b>ReversiDirection</b>: Enum representing the eight possible movement directions.</li>
 * </ul>
 *
 * <p>All classes in this package are designed to be **immutable, reusable, and focused solely
 * on the data representation of Reversi**.</p>
 *
 * @see ReversiBoard
 * @see ReversiPiece
 * @see ReversiColor
 * @see ReversiDirection
 */
package reversi.model
