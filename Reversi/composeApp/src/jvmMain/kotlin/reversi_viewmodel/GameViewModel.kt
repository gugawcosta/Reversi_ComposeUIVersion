package reversi_viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import reversi.core.Reversi
import reversi.core.ReversiAction
import reversi.core.ReversiState
import reversi.framework.Cell
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import reversi.model.boardToMongoString
import reversi.model.mongoStringToBoard
import reversi_data.mongodb.MongoGameManager
import java.io.File

/**
 * ViewModel para gerir o estado e lógica do jogo Reversi.
 * @param game Instância do jogo Reversi.
 * @param gameName Nome do jogo (vazio para singleplayer).
 * @param localPlayerColor Cor do jogador local (default: Branco).
 * @param dispatcher Dispatcher para operações assíncronas (default: IO).
 */
class GameViewModel(
    val game: Reversi,
    val gameName: String,
    val localPlayerColor: ReversiColor = ReversiColor.WHITE,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(dispatcher)

    val isMultiplayer: Boolean
        get() = gameName.isNotBlank()

    var currentState by mutableStateOf(game.currentState)
        private set

    var showTargets by mutableStateOf(false)
        private set
    var autoRefreshEnabled by mutableStateOf(isMultiplayer)
    var statusMessage by mutableStateOf(if (isMultiplayer) "A carregar jogo..." else "Modo Singleplayer")

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var isProcessingMove by mutableStateOf(false)

    val currentPlayer: ReversiColor get() = currentState.currentTurn
    val blackCount: Int get() = currentState.score.black
    val whiteCount: Int get() = currentState.score.white
    val gameOver: Boolean get() = currentState.isOver

    /**
     * Alterna entre mostrar ou não as possíveis jogadas no tabuleiro.
     *
     */
    fun toggleTargets() { showTargets = !showTargets }

    /**
     * Ativa ou desativa o auto-refresh para jogos multiplayer.
     */
    fun toggleAutoRefresh() { if (isMultiplayer) autoRefreshEnabled = !autoRefreshEnabled }

    /**
     * Processa o clique numa célula do tabuleiro.
     * @param cell Célula clicada.
     */
    fun onCellClick(cell: Cell) {
        errorMessage = null

        if (isMultiplayer && currentPlayer != localPlayerColor) {
            statusMessage = "Não é a tua vez!"
            return
        }
        if (isProcessingMove) return

        val action = ReversiAction(cell)
        if (action !in currentState.legalMoves) return

        val newState = currentState.applyAction(action)
        updateLocalState(newState)

        if (!isMultiplayer) {
            statusMessage = "Vez das ${if(newState.currentTurn == ReversiColor.BLACK) "Pretas" else "Brancas"}"
            return
        }

        isProcessingMove = true
        statusMessage = "A enviar jogada..."

        scope.launch {
            try {
                updateGameInternal(newState)
                // Grava no Mongo
                val success = MongoGameManager.updateGameState(gameName, game)

                withContext(Dispatchers.Main) {
                    if (success) {
                        statusMessage = "Jogada enviada. Aguarda adversário..."
                    } else {
                        errorMessage = "Falha ao sincronizar."
                        statusMessage = "Erro ao enviar."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { errorMessage = "Erro de conexão." }
            } finally {
                isProcessingMove = false
            }
        }
    }

    /**
     * Processa a ação de passar a vez.
     */
    fun passTurn() {
        errorMessage = null
        if (isMultiplayer && currentPlayer != localPlayerColor) return
        if (isProcessingMove) return

        val newState = currentState.applyAction(ReversiAction.PASS)
        updateLocalState(newState)

        if (!isMultiplayer) {
            statusMessage = "Passaste a vez."
            return
        }

        scope.launch {
            try {
                updateGameInternal(newState)
                MongoGameManager.updateGameState(gameName, game)
                withContext(Dispatchers.Main) { statusMessage = "Passaste a vez." }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { errorMessage = "Erro ao passar vez." }
            } finally {
                isProcessingMove = false
            }
        }
    }

    /**
     * Atualiza o estado do jogo a partir do servidor remoto.
     */
    fun refreshGame() {
        if (!isMultiplayer) return
        if (isProcessingMove) return

        scope.launch {
            try {
                val remoteState = MongoGameManager.loadGameState(gameName)

                if (remoteState != null) {
                    val remoteBoardStr = remoteState.board
                    val localBoardStr = boardToMongoString(currentState.pieces, game.board)

                    val remoteTurnStr = remoteState.turn.uppercase()
                    val remoteTurn = if (remoteTurnStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK

                    if (remoteBoardStr != localBoardStr || remoteTurn != currentState.currentTurn) {

                        val newPieces = mongoStringToBoard(remoteBoardStr, game.board)

                        val updatedState = ReversiState(
                            pieces = newPieces,
                            currentTurn = remoteTurn,
                            consecutivePasses = 0,
                            board = game.board
                        )

                        updateLocalState(updatedState)
                        updateGameInternal(updatedState)

                        withContext(Dispatchers.Main) {
                            errorMessage = null
                            statusMessage = if (remoteTurn == localPlayerColor) "A TUA VEZ!" else "Vez do adversário."
                        }
                    }
                }
            } catch (e: Exception) {
                println("Erro refresh: ${e.message}")
            }
        }
    }

    /**
     * Atualiza o estado local do jogo.
     * @param newState Novo estado do jogo.
     */
    private fun updateLocalState(newState: ReversiState) {
        currentState = newState
    }

    /**
     * Atualiza o estado do jogo na instância do Reversi.
     * @param newState Novo estado do jogo.
     */
    private fun updateGameInternal(newState: ReversiState) {
        try { game.currentState = newState } catch (e: Exception) { }
    }

    /**
     * Salva um backup local do estado atual do jogo.
     */
    fun saveLocalBackup() {
        try {
            val folder = File("game_states")
            if (!folder.exists()) folder.mkdirs()

            val filename = "${gameName}.txt"
            val file = File(folder, filename)
            val boardStr = boardToMongoString(currentState.pieces, game.board)
            val turnStr = currentState.currentTurn.toString()

            val content = "Game: $gameName\nTurn: $turnStr\nBoard: $boardStr"
            file.writeText(content)

        } catch (e: Exception) {
            println("ERRO BACKUP: ${e.message}")
        }
    }

    /**
     * Obtém a peça na célula especificada.
     * @param cell Célula a consultar.
     * @return Peça na célula ou null se estiver vazia.
     */
    fun getPieceAt(cell: Cell): ReversiPiece? = currentState.pieces[cell]
}