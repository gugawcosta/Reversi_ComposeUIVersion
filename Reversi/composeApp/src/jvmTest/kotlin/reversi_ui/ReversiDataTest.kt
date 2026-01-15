package reversi_ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reversi.core.Reversi
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import reversi.model.boardToMongoString
import reversi.model.mongoStringToBoard

class ReversiDataTest {

    @Test
    fun `test board serialization to string 8x8`() {
        // 1. Preparar dados
        val board = ReversiBoard(8, 8)
        val pieces = mutableMapOf<Cell, ReversiPiece>()
        pieces[Cell(4, 4)] = ReversiPiece(Cell(4, 4), ReversiColor.WHITE)
        pieces[Cell(4, 5)] = ReversiPiece(Cell(4, 5), ReversiColor.BLACK)

        // 2. Converter
        val mongoString = boardToMongoString(pieces, board)

        println("DEBUG: String gerada: '$mongoString'")

        // 3. Normalizar (remover quebras de linha para testar o tamanho real)
        val cleanString = mongoString.replace("\n", "").replace("\r", "")

        // Verificar comprimento (deve ser 64 se for 8x8)
        assertEquals(64, cleanString.length, "A string deve ter 64 caracteres")

        // 4. Verificar conteúdo
        // Verifica se as peças W e B estão lá
        assertTrue(cleanString.contains("W"), "Deve conter 'W'")
        assertTrue(cleanString.contains("B"), "Deve conter 'B'")

        // Verifica vazios ('-')
        val emptyCount = cleanString.count { it == '-' }
        assertTrue(emptyCount > 50, "Deve ter mais de 50 células vazias. Encontradas: $emptyCount")
    }

    @Test
    fun `test full cycle serialization and deserialization`() {
        // 1. Criar jogo original e fazer uma jogada simulada
        val originalGame = Reversi(ReversiBoard(8, 8))
        // Adiciona uma peça extra manualmente para teste
        val testCell = Cell(1, 1)
        val newPieces = originalGame.currentState.pieces.toMutableMap()
        newPieces[testCell] = ReversiPiece(testCell, ReversiColor.BLACK)

        // 2. Serializar (Board -> String)
        val boardStr = boardToMongoString(newPieces, originalGame.board)

        // 3. Deserializar (String -> Board)
        val restoredPieces = mongoStringToBoard(boardStr, originalGame.board)

        // 4. Asserções
        assertEquals(newPieces.size, restoredPieces.size, "Deve ter o mesmo número de peças")

        // Verifica se a peça manual existe no recuperado
        val pieceAt1_1 = restoredPieces[testCell]
        assertEquals(ReversiColor.BLACK, pieceAt1_1?.color, "A peça em (1,1) deve ser Preta")
    }

    @Test
    fun `test variable board size serialization`() {
        // Testar tabuleiro 4x4
        val board4 = ReversiBoard(4, 4)
        val pieces = mapOf(Cell(1,1) to ReversiPiece(Cell(1,1), ReversiColor.WHITE))

        val result = boardToMongoString(pieces, board4)

        assertEquals(16, result.length, "Tabuleiro 4x4 deve gerar string de 16 chars")
    }
}