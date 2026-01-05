package reversi.core

import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiDirection
import reversi.model.ReversiPiece
import reversi.framework.Cell
import kotlin.collections.iterator

/**
 * Determines whether the game is over.
 *
 * A game ends when:
 * 1. The board is filled.
 * 2. Both players have consecutively passed the maximum allowed turns.
 *
 * @receiver [ReversiState] the current immutable game state.
 * @param board the [ReversiBoard] associated with the game.
 * @return `true` if the game has ended, otherwise `false`.
 */
fun ReversiState.reversiIsOver(board: ReversiBoard): Boolean {
    if (score.white + score.black == board.totalCells) // The Board is full
        return true
    if (consecutivePasses >= MAXIMUM_CONSECUTIVE_PASSES) // Both players passed
        return true
    return false
}

/**
 * Calculates the current result of the game based on the state and board.
 *
 * Returns [ReversiResult.ONGOING] if the game is not over.
 * Otherwise, returns the winning color or [ReversiResult.DRAW] if tied.
 *
 * @receiver [ReversiState] the current game state.
 * @param board the [ReversiBoard] associated with the game.
 * @return the [ReversiResult] representing the game outcome.
 */
fun ReversiState.reversiGetResult(board: ReversiBoard): ReversiResult {
    if (!reversiIsOver(board))
        return ReversiResult.ONGOING

    if (score.white > score.black)
        return ReversiResult.WHITE_WINS

    if (score.white < score.black)
        return ReversiResult.BLACK_WINS

    return ReversiResult.DRAW
}

/**
 * Applies a given [ReversiAction] to the current state, producing a new state.
 *
 * If [action].position is null, the action represents a "pass".
 * Otherwise, places a piece at the given position and flips any sandwiched opponent pieces
 * along all 8 directions.
 *
 * @receiver [ReversiState] the current immutable game state.
 * @param action the [ReversiAction] to apply.
 * @param board the [ReversiBoard] associated with the game.
 * @return a new [ReversiState] reflecting the result of the action.
 */

fun ReversiState.reversiApplyAction(action: ReversiAction, board: ReversiBoard): ReversiState {
    val currentColor = currentTurn
    val opponentColor = currentColor.invertColor()

    // Handle pass (no move)
    if (action.position == null) {
        return copy(
            currentTurn = opponentColor,
            consecutivePasses = consecutivePasses + 1
        )
    }

    val newPieces = pieces.toMutableMap()
    val pos = action.position

    // Place the new piece
    newPieces[pos] = ReversiPiece(pos, currentColor)

    // Flip pieces in all 8 directions
    for (dir in ReversiDirection.entries) {
        var row = pos.row
        var col = pos.col
        val toFlip = mutableListOf<Cell>()

        while (true) { // Usar while(true) em vez de do-while para controlar melhor o break
            row += dir.dx
            col += dir.dy

            // 1. Verificação de segurança PRIMEIRO
            if (!board.isValid(Cell(row, col))) break

            val currentCell = Cell(row, col)
            val currentPiece = newPieces[currentCell]

            // 2. Se a casa está vazia, a linha quebrou (não vira nada nesta direção)
            if (currentPiece == null) break

            // 3. Se é oponente, adiciona à lista de potenciais capturas
            if (currentPiece.color == opponentColor) {
                toFlip.add(currentCell)
                continue
            }

            // 4. Se é a nossa cor, confirma a captura!
            if (currentPiece.color == currentColor) {
                toFlip.forEach { flipPos ->
                    newPieces[flipPos] = ReversiPiece(flipPos, currentColor)
                }
                break // Captura feita, sai desta direção
            }
        }
    }

    return copy(
        pieces = newPieces,
        currentTurn = opponentColor,
        consecutivePasses = 0
    )
}

/**
 * Computes all legal moves for the current player in the given state.
 *
 * Scans from each piece of the current color in all 8 directions and identifies
 * empty cells that would capture opponent pieces.
 *
 * @receiver [ReversiState] the current game state.
 * @param board the [ReversiBoard] associated with the game.
 * @return a set of [ReversiAction] representing all legal moves.
 */

fun ReversiState.reversiGetLegalMoves(board: ReversiBoard): Set<ReversiAction> {
    val legalMoves = mutableSetOf<ReversiAction>()
    val currentColor = currentTurn

    // 1. We define this INSIDE the function so it can see 'board' and 'pieces'
    fun octaDirectionalLaserCheck(originPiece: ReversiPiece) {
        val opponentColor = currentColor.invertColor()

        for (dir in ReversiDirection.entries) {
            var row = originPiece.position.row
            var col = originPiece.position.col
            var foundOpponent = false

            while (true) {
                row += dir.dx
                col += dir.dy

                // FIX: Check bounds first to prevent crashes
                if (!board.isValid(Cell(row, col))) break

                val currentCell = Cell(row, col)
                val pieceAtCell = pieces[currentCell]

                if (pieceAtCell == null) {
                    // We found an empty square.
                    // It is a valid move ONLY if we sandwiched opponent pieces.
                    if (foundOpponent) {
                        legalMoves.add(ReversiAction(currentCell))
                    }
                    break // Stop scanning this direction
                }

                if (pieceAtCell.color == opponentColor) {
                    foundOpponent = true
                    continue // Keep going to look for the empty spot behind them
                }

                // We hit our own color -> blocked line
                break
            }
        }
    }

    // 2. The Main Loop
    // We iterate over OUR pieces and look for lines extending from them
    for ((_, piece) in pieces) {
        if (piece.color == currentColor) {
            octaDirectionalLaserCheck(piece)
        }
    }
    return legalMoves
}

/**
 * Calculates the current score based on the positions of pieces.
 *
 * @receiver a map of [Cell] to [ReversiPiece] representing the current pieces on the board.
 * @return a [ReversiScore] with counts of white and black pieces.
 */
fun Map<Cell, ReversiPiece>.getScore(): ReversiScore {
    var white = 0
    var black = 0

    for ((_, piece) in this) {
        if (piece.color == ReversiColor.WHITE)
            white++
        else
            black++
    }

    return ReversiScore(white, black)
}

