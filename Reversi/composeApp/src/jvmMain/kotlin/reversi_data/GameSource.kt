package reversi_data

import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi_data.local.LocalGameManager
import reversi_data.model.GameState
import reversi_data.mongodb.MongoGameManager

/**
 * Interface que define as operações obrigatórias para qualquer fonte de dados (Local ou Cloud).
 */
interface IGameManager {
    suspend fun getAllGames(): List<GameState>
    suspend fun createNewGame(name: String, game: Reversi, creatorColor: ReversiColor, creatorId: String, size: Int): Boolean
    suspend fun joinGameAsPlayer2(gameName: String, myId: String): Boolean
    suspend fun loadGameState(name: String): GameState?
    suspend fun updateGameState(name: String, game: Reversi): Boolean
}

/**
 * Objeto global que guarda a preferência do utilizador e fornece o Gestor Ativo.
 */
object AppConfig {
    // Variável que muda quando clicas no botão do StartScreen
    var useLocalSource: Boolean = false

    // Devolve o MongoGameManager ou o LocalGameManager consoante a escolha
    val activeManager: IGameManager
        get() = if (useLocalSource) LocalGameManager else MongoGameManager
}