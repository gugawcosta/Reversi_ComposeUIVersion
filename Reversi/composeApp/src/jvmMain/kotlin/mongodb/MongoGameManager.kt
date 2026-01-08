package mongodb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mongodb.client.model.Filters
import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi.model.boardToMongoString

import com.mongodb.client.model.Sorts

/**
 * Objeto responsável por gerir a criação, atualização e carregamento de jogos
 * na base de dados MongoDB.
 * Utiliza corrotinas para operações assíncronas.
 * Baseia-se na coleção tipada [MongoRepository.gamesCollection].
 */

object MongoGameManager {

    /**
     * Função para criar um jogo na base de dados.
     * Retorna true se o jogo foi criado com sucesso, false se o nome já existe
     * ou em caso de erro.
     * @param name Nome do jogo (ID na BD)
     * @param game Instância do jogo Reversi
     * @param creatorColor Cor do jogador que criou o jogo
     */
    suspend fun createNewGame(name: String, game: Reversi, creatorColor: ReversiColor): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (MongoRepository.gamesCollection.find(Filters.eq("_id", name))
                        .first() != null
                ) return@withContext false

                val boardString = boardToMongoString(game.currentState.pieces, game.currentState.board)

                val turnStr = game.currentState.currentTurn.toString() // "BLACK" ou "WHITE"
                val p1ColorStr = creatorColor.toString()

                val newGameState = GameState(
                    gameName = name,
                    board = boardString,
                    turn = turnStr,
                    p1Color = p1ColorStr,
                    timestamp = System.currentTimeMillis()
                )

                MongoRepository.gamesCollection.insertOne(newGameState)
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    /**
     * Função para atualizar o estado de um jogo existente na base de dados.
     * @param name Nome do jogo (ID na BD)
     * @param game Instância do jogo Reversi com o estado atualizado
     */
    suspend fun updateGameState(name: String, game: Reversi) =
        withContext(Dispatchers.IO) {
            try {
                val oldState = MongoRepository.gamesCollection.find(Filters.eq("_id", name)).first()
                if (oldState != null) {
                    val boardString = boardToMongoString(game.currentState.pieces, game.currentState.board)
                    val nextTurnString = game.currentState.currentTurn.toString() // O turno JÁ ATUALIZADO localmente

                    val updatedGame = GameState(
                        gameName = name,
                        board = boardString,
                        turn = nextTurnString,
                        p1Color = oldState.p1Color, // Preserva quem criou
                        timestamp = System.currentTimeMillis()
                    )

                    MongoRepository.gamesCollection.replaceOne(Filters.eq("_id", name), updatedGame)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    /**
     * Função para carregar o estado de um jogo da base de dados.
     * Retorna o [GameState] se encontrado, ou null em caso de erro ou
     * se o jogo não existir.
     * @param name Nome do jogo (ID na BD)
     * @return Instância de [GameState] ou null
     */
    suspend fun loadGameState(name: String): GameState? =
        withContext(Dispatchers.IO) {
            try {
                return@withContext MongoRepository.gamesCollection.find(Filters.eq("_id", name)).first()
            } catch (e: Exception) {
                return@withContext null
            }
        }

    /**
     * Retorna todos os jogos existentes na coleção, ordenados pelos mais recentes.
     * Limita a 50 jogos para evitar sobrecarga de memória.
     */
    suspend fun getAllGames(): List<GameState> =
        withContext(Dispatchers.IO) {
            try {
                // Find vazio = procura tudo. Sort desc (mais recentes primeiro)
                return@withContext MongoRepository.gamesCollection
                    .find()
                    .sort(Sorts.descending("timestamp"))
                    .limit(50)
                    .into(ArrayList())
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
}