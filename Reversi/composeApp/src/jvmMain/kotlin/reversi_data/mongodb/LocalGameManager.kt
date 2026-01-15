package reversi_data.mongodb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import reversi.core.Reversi
import reversi.model.ReversiColor
import java.io.File

/**
 * Gestor que lê e escreve ficheiros na pasta local 'game_states'.
 */
object LocalGameManager : IGameManager {

    private val folder = File("game_states")

    init {
        if (!folder.exists()) folder.mkdirs()
    }

    override suspend fun getAllGames(): List<GameState> = withContext(Dispatchers.IO) {
        val games = mutableListOf<GameState>()

        if (!folder.exists()) return@withContext emptyList()

        // Lê todos os ficheiros .txt da pasta
        folder.listFiles()?.filter { it.extension == "txt" }?.forEach { file ->
            try {
                val lines = file.readLines()
                // Parse simples do formato "Chave: Valor"
                val name = lines.find { it.startsWith("Game:") }?.substringAfter(":")?.trim() ?: file.nameWithoutExtension
                val turn = lines.find { it.startsWith("Turn:") }?.substringAfter(":")?.trim() ?: "BLACK"
                val board = lines.find { it.startsWith("Board:") }?.substringAfter(":")?.trim() ?: ""

                // Tenta descobrir o tamanho do tabuleiro pela raiz quadrada da string
                val size = kotlin.math.sqrt(board.length.toDouble()).toInt()

                // Cria um GameState compatível com o Lobby
                val state = GameState(
                    gameName = name,
                    board = board,
                    turn = turn,
                    p1Color = "BLACK", // Em local assumimos Pretas como base
                    timestamp = file.lastModified(),
                    boardSize = if (size > 0) size else 8,
                    player1Id = "LocalUser",
                    player2Id = null,
                )
                games.add(state)
            } catch (e: Exception) {
                println("Erro ao ler ficheiro local: ${file.name}")
            }
        }
        // Ordena por mais recente
        return@withContext games.sortedByDescending { it.timestamp }
    }

    // Em modo local, 'criar' é apenas permitir avançar (o ficheiro é criado ao gravar/sair)
    override suspend fun createNewGame(name: String, game: Reversi, creatorColor: ReversiColor, creatorId: String, size: Int): Boolean = true

    // Em modo local, podemos sempre 'entrar'
    override suspend fun joinGameAsPlayer2(gameName: String, myId: String): Boolean = true

    // Carrega o jogo lendo da lista (reutiliza lógica do getAllGames)
    override suspend fun loadGameState(name: String): GameState? = withContext(Dispatchers.IO) {
        return@withContext getAllGames().find { it.gameName == name }
    }

    // O update em local pode ser ignorado aqui, pois o ViewModel já grava o ficheiro com 'saveLocalBackup'
    override suspend fun updateGameState(name: String, game: Reversi): Boolean = true
}