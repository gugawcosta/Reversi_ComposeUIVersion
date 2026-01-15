package reversi_data.mongodb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Sorts
import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi.model.boardToMongoString
import reversi_data.model.GameState
import reversi_data.IGameManager

/**
 * Gestor que lê e escreve estados de jogo numa base de dados MongoDB.
 * Utiliza a coleção 'games' definida em MongoRepository.
 */
object MongoGameManager : IGameManager {

    /**
     * Cria um jogo na base de dados.
     * Verifica se o nome do jogo já existe antes de criar.
     * @param name Nome do jogo.
     * @param game Instância do jogo Reversi.
     * @param creatorColor Cor do jogador que criou o jogo.
     * @param creatorId ID do jogador que criou o jogo.
     * @param size Tamanho do tabuleiro.
     * @return true se o jogo foi criado com sucesso, false caso contrário.
     */
    override suspend fun createNewGame(name: String, game: Reversi, creatorColor: ReversiColor, creatorId: String, size: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (MongoRepository.gamesCollection.find(Filters.eq("_id", name)).first() != null)
                    return@withContext false

                val boardString = boardToMongoString(game.currentState.pieces, game.currentState.board)

                val newGameState = GameState(
                    gameName = name,
                    board = boardString,
                    turn = game.currentState.currentTurn.toString(),
                    p1Color = creatorColor.toString(),
                    timestamp = System.currentTimeMillis(),
                    boardSize = size,
                    player1Id = creatorId,
                    player2Id = null,
                )

                MongoRepository.gamesCollection.insertOne(newGameState)
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    /**
     * Permite a um jogador juntar-se a um jogo existente como 'Jogador 2'.
     * Verifica se o jogo existe e se já não tem um 'Jogador 2'.
     * @param gameName Nome do jogo.
     * @param myId ID do jogador que está a tentar juntar-se.
     * @return true se o jogador conseguiu juntar-se, false caso contrário.
     */
    override suspend fun joinGameAsPlayer2(gameName: String, myId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val game = MongoRepository.gamesCollection.find(Filters.eq("_id", gameName)).first() ?: return@withContext false

                if (game.player1Id == myId || game.player2Id == myId) return@withContext true
                if (game.player2Id != null && game.player2Id!!.isNotBlank()) return@withContext false

                val result = MongoRepository.gamesCollection.updateOne(
                    Filters.eq("_id", gameName),
                    Updates.set("player2Id", myId)
                )

                return@withContext result.modifiedCount > 0 || result.matchedCount > 0
            } catch (e: Exception) {
                return@withContext false
            }
        }

    /**
     * Atualiza o estado do jogo na base de dados.
     * @param name Nome do jogo.
     * @param game Instância do jogo Reversi.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    override suspend fun updateGameState(name: String, game: Reversi): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val boardString = boardToMongoString(game.currentState.pieces, game.currentState.board)
                val nextTurnString = game.currentState.currentTurn.toString()

                val updates = Updates.combine(
                    Updates.set("board", boardString),
                    Updates.set("turn", nextTurnString),
                    Updates.set("timestamp", System.currentTimeMillis())
                )

                val result = MongoRepository.gamesCollection.updateOne(Filters.eq("_id", name), updates)
                return@withContext result.modifiedCount > 0 || result.matchedCount > 0
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    /**
     * Carrega o estado do jogo a partir da base de dados.
     * @param name Nome do jogo.
     * @return Instância de GameState se o jogo for encontrado, null caso contrário.
     */
    override suspend fun loadGameState(name: String): GameState? =
        withContext(Dispatchers.IO) {
            try {
                return@withContext MongoRepository.gamesCollection.find(Filters.eq("_id", name)).first()
            } catch (e: Exception) {
                return@withContext null
            }
        }

    /**
     * Obtém uma lista dos jogos mais recentes da base de dados.
     * Limita a 50 jogos ordenados por timestamp decrescente.
     * @return Lista de GameState.
     * @see GameState
     */
    override suspend fun getAllGames(): List<GameState> =
        withContext(Dispatchers.IO) {
            try {
                return@withContext MongoRepository.gamesCollection
                    .find()
                    .sort(Sorts.descending("timestamp"))
                    .limit(50)
                    .into(ArrayList())
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }
}