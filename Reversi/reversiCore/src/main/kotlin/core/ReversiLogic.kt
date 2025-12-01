package reversi.core

import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiDirection
import reversi.model.ReversiPiece
import reversi.framework.Cell
import kotlin.collections.iterator
import kotlin.collections.plusAssign

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

    // Flip pieces in all 8 directions if they are sandwiched
    for (dir in ReversiDirection.entries) {
        var row = pos.row
        var col = pos.col
        val toFlip = mutableListOf<Cell>()

        do {
            row += dir.dx
            col += dir.dy

            val currentPiece = newPieces[Cell(row, col)] ?: break // Empty — not flippable

            if (currentPiece.color == opponentColor) {  // Potentially flippable
                toFlip += Cell(row, col)
                continue
            }

            if (currentPiece.color == currentColor) {   // Found our own color → confirm flip
                toFlip.forEach { flipPos ->
                    newPieces[flipPos] = ReversiPiece(flipPos, currentColor)
                }
            }

            break
        } while (board.isValid(Cell(row, col)))
    }

    // Return updated state
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

    fun octaDirectionalLaserCheck(piece: ReversiPiece): Set<ReversiAction> {
        val legalMoves = mutableSetOf<ReversiAction>()
        val currentColor = piece.color
        val opponentColor = currentColor.invertColor()

        for (dir in ReversiDirection.entries) {
            var row = piece.position.row
            var col = piece.position.col
            var foundOpponent = false

            do {
                row += dir.dx
                col += dir.dy
                val currentPiece = pieces[Cell(row, col)]

                if (currentPiece == null) {
                    // Empty square — valid only if we passed at least one opponent piece
                    if (foundOpponent)
                        legalMoves += ReversiAction(Cell(row, col))
                    break
                }

                if (currentPiece.color == opponentColor) {
                    foundOpponent = true // Keep scanning if it's an opponent piece
                    continue
                }

                break // Found our own color — stop scanning this direction

            } while (board.isValid(Cell(row, col)))
        }

        return legalMoves
    }

    val mainSet = mutableSetOf<ReversiAction>()
    val currentColor = currentTurn

    for ((_, piece) in pieces)
        if (piece.color == currentColor)
            mainSet += octaDirectionalLaserCheck(piece)

    return mainSet
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

/**
 * Aplica a ação e devolve o novo estado + lista das posições viradas (ordem perto -> longe).
 */
fun ReversiState.reversiApplyActionWithFlips(action: ReversiAction, board: ReversiBoard): Pair<ReversiState, List<Cell>> {
    val currentColor = currentTurn
    val opponentColor = currentColor.invertColor()

    // Pass (sem posição)
    if (action.position == null) {
        val nextState = copy(
            currentTurn = opponentColor,
            consecutivePasses = consecutivePasses + 1
        )
        return Pair(nextState, emptyList())
    }

    val newPieces = pieces.toMutableMap()
    val pos = action.position

    // Coloca a peça nova
    newPieces[pos] = ReversiPiece(pos, currentColor)

    val allFlipped = mutableListOf<Cell>()

    for (dir in ReversiDirection.entries) {
        var row = pos.row
        var col = pos.col
        val toFlip = mutableListOf<Cell>()

        while (true) {
            row += dir.dx
            col += dir.dy
            val cell = Cell(row, col)

            if (!board.isValid(cell)) { // fora do tabuleiro
                toFlip.clear()
                break
            }

            val currentPiece = newPieces[cell] ?: run {
                toFlip.clear() // encontrou vazio >> não vira nesta direção
                break
            }

            if (currentPiece.color == opponentColor) {
                toFlip += cell
                continue
            }

            if (currentPiece.color == currentColor) {
                // confirma virar >> ficaram em toFlip (já estão em ordem próximo >> longe)
                allFlipped += toFlip
            }
            break
        }
    }

    // Aplica flips
    for (flipPos in allFlipped) {
        newPieces[flipPos] = ReversiPiece(flipPos, currentColor)
    }

    val newState = copy(
        pieces = newPieces,
        currentTurn = opponentColor,
        consecutivePasses = 0
    )

    return Pair(newState, allFlipped)
}

