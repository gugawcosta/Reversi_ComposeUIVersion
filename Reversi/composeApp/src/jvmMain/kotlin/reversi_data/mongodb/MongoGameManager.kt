package reversi_data.mongodb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Sorts
import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi.model.boardToMongoString

/**
 * Objeto responsável por gerir a criação, atualização e carregamento de jogos
 * na base de dados MongoDB.
 */
object MongoGameManager {

    /**
     * Cria um novo jogo com suporte a ID do jogador e Tamanho do Tabuleiro.
     */
    suspend fun createNewGame(name: String, game: Reversi, creatorColor: ReversiColor, creatorId: String, size: Int): Boolean =
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
                    player1Id = creatorId,   // Guarda o ID do criador
                    player2Id = null         // Ainda ninguém entrou
                )

                MongoRepository.gamesCollection.insertOne(newGameState)
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    /**
     * Tenta juntar-se a um jogo.
     * 1. Se eu já sou o Player 1 ou Player 2, entro (Re-join).
     * 2. Se o Player 2 está livre, ocupo o lugar.
     * 3. Se o Player 2 está ocupado por outro, bloqueia.
     */
    suspend fun joinGameAsPlayer2(gameName: String, myId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val game = MongoRepository.gamesCollection.find(Filters.eq("_id", gameName)).first() ?: return@withContext false

                // 1. Se eu já sou um dos jogadores registados, autoriza entrada
                if (game.player1Id == myId || game.player2Id == myId) return@withContext true

                // 2. Se o lugar do Player 2 já está ocupado (e não sou eu), bloqueia
                if (game.player2Id != null && game.player2Id!!.isNotBlank()) return@withContext false

                // 3. Se está livre, ocupa o lugar atomicamente na BD
                val result = MongoRepository.gamesCollection.updateOne(
                    Filters.eq("_id", gameName),
                    Updates.set("player2Id", myId)
                )

                // Retorna true se conseguiu escrever na BD
                return@withContext result.modifiedCount > 0 || result.matchedCount > 0
            } catch (e: Exception) {
                return@withContext false
            }
        }

    /**
     * Atualiza o estado do jogo existente na base de dados.
     * Retorna true se a atualização foi bem sucedida.
     */
    suspend fun updateGameState(name: String, game: Reversi): Boolean =
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
     * Carrega o estado do jogo pelo nome.
     * Retorna null se não encontrado ou em caso de erro.
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
     * Obtém a lista dos últimos (50) jogos ordenados por timestamp decrescente.
     * Retorna uma lista vazia em caso de erro.
     */
    suspend fun getAllGames(): List<GameState> =
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