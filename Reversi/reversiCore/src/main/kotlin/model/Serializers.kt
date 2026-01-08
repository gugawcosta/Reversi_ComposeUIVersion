package reversi.model

import reversi.framework.Cell

// Caracteres para guardar na Base de Dados
private const val CHAR_WHITE = 'W'
private const val CHAR_BLACK = 'B'
private const val CHAR_EMPTY = '-'

/**
 * Converte o mapa de peças atual numa String para guardar no MongoDB.
 * Lê linha a linha, da esquerda para a direita.
 */
fun boardToMongoString(pieces: Map<Cell, ReversiPiece>, board: ReversiBoard): String {
    val sb = StringBuilder()

    // Percorre todas as células do tabuleiro
    for (row in 1..board.height) {
        for (col in 1..board.width) {
            val cell = Cell(row, col)
            val piece = pieces[cell]

            // Verifica a cor da peça e adiciona o char correspondente
            val char = when (piece?.color) {
                ReversiColor.WHITE -> CHAR_WHITE
                ReversiColor.BLACK -> CHAR_BLACK
                else -> CHAR_EMPTY
            }
            sb.append(char)
        }
    }
    return sb.toString()
}

/**
 * Converte a String que vem do MongoDB de volta num mapa de peças para o jogo usar.
 */
fun mongoStringToBoard(dbString: String, board: ReversiBoard): Map<Cell, ReversiPiece> {
    val pieces = mutableMapOf<Cell, ReversiPiece>()
    var index = 0

    for (row in 1..board.height) {
        for (col in 1..board.width) {
            // Proteção para não rebentar se a string for curta
            if (index >= dbString.length) break

            val char = dbString[index]
            val cell = Cell(row, col)

            // Se for W ou B, cria a peça e mete no mapa
            when (char) {
                CHAR_WHITE -> pieces[cell] = ReversiPiece(cell, ReversiColor.WHITE)
                CHAR_BLACK -> pieces[cell] = ReversiPiece(cell, ReversiColor.BLACK)
            }
            index++
        }
    }
    return pieces
}