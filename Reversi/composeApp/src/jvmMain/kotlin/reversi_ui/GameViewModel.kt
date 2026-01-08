package reversi_ui

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import reversi.core.Reversi
import reversi.core.ReversiAction
import reversi.core.ReversiState
import reversi.model.ReversiColor
import reversi.model.ReversiPiece
import reversi.framework.Cell
import reversi.model.boardToMongoString
import reversi.model.mongoStringToBoard
import mongodb.MongoGameManager
import java.io.File

/**
 * ViewModel para gerir o estado e lógica do jogo Reversi.
 * Suporta tanto jogos locais (single-player) como multiplayer via MongoDB.
 * @param game Instância do jogo Reversi.
 * @param gameName Nome do jogo (vazio se local).
 * @param localPlayerColor Cor do jogador local (apenas relevante em multiplayer).
 */

class GameViewModel(
    val game: Reversi,
    val gameName: String, // se for vazio (""), é single-player
    val localPlayerColor: ReversiColor = ReversiColor.WHITE
) {

    // Scope para operações assíncronas
    private val scope = CoroutineScope(Dispatchers.IO)

    // Determina se é multiplayer
    val isMultiplayer: Boolean
        get() = gameName.isNotBlank()

    // Estado local da UI
    var currentState by mutableStateOf(game.currentState)
        private set

    // Controlo de UI
    var showTargets by mutableStateOf(false)
        private set
    var autoRefreshEnabled by mutableStateOf(isMultiplayer)
    var statusMessage by mutableStateOf(if (isMultiplayer) "A carregar jogo..." else "Modo Singleplayer")

    // Bloqueio temporário para evitar conflitos durante o processamento de jogadas
    private var isProcessingMove by mutableStateOf(false)

    // Propriedades derivadas
    val currentPlayer: ReversiColor get() = currentState.currentTurn
    val blackCount: Int get() = currentState.score.black
    val whiteCount: Int get() = currentState.score.white
    val gameOver: Boolean get() = currentState.isOver

    fun toggleTargets() { showTargets = !showTargets }

    // Alterna o estado do Auto-Refresh (apenas em multiplayer)
    fun toggleAutoRefresh() { if (isMultiplayer) autoRefreshEnabled = !autoRefreshEnabled }

    /**
     * Lida com o clique numa célula do tabuleiro.
     * @param cell A célula clicada.
     */
    fun onCellClick(cell: Cell) {
        if (isMultiplayer && currentPlayer != localPlayerColor) {
            statusMessage = "Não é a tua vez!"
            return
        }
        if (isProcessingMove) return // Evita duplo clique

        val action = ReversiAction(cell)
        if (action !in currentState.legalMoves) return

        // 1. Aplica a jogada localmente (UI reage logo)
        val newState = currentState.applyAction(action)
        updateLocalState(newState)

        if (!isMultiplayer) {
            statusMessage = "Vez das ${if(newState.currentTurn == ReversiColor.BLACK) "Pretas" else "Brancas"}"
            return
        }

        // Bloqueia o Refresh para a peça não desaparecer
        isProcessingMove = true
        statusMessage = "A enviar jogada..."

        // 2. Envia para a Cloud
        scope.launch {
            try {
                // Atualiza o objeto interno do jogo para bater certo
                updateGameInternal(newState)

                // Grava no Mongo
                MongoGameManager.updateGameState(gameName, game)

                withContext(Dispatchers.Main) {
                    statusMessage = "Jogada enviada. Aguarda adversário..."
                }
            } catch (e: Exception) {
                println("Erro ao enviar: ${e.message}")
            } finally {
                // 3. Liberta o Refresh (Só agora é que permitimos ler da BD novamente)
                isProcessingMove = false
            }
        }
    }

    /**
     * Lida com a ação de passar a vez.
     * Apenas permitido se não houver jogadas válidas.
     */
    fun passTurn() {
        if (isMultiplayer && currentPlayer != localPlayerColor) return

        if (isProcessingMove) return

        val newState = currentState.applyAction(ReversiAction.PASS)
        updateLocalState(newState)

        if (!isMultiplayer) {
            statusMessage = "Passaste a vez. Agora joga: ${newState.currentTurn}"
            return
        }

        scope.launch {
            try {
                updateGameInternal(newState)
                MongoGameManager.updateGameState(gameName, game)
                withContext(Dispatchers.Main) {
                    statusMessage = "Passaste a vez."
                }
            } finally {
                isProcessingMove = false // Desbloqueia
            }
        }
    }

    /**
     * Faz refresh do estado do jogo a partir do MongoDB.
     * Apenas funciona em jogos multiplayer.
     */
    fun refreshGame() {
        if (!isMultiplayer) return
        if (isProcessingMove) return

        scope.launch {
            val remoteState = MongoGameManager.loadGameState(gameName)

            if (remoteState != null) {
                val remoteBoardStr = remoteState.board
                val localBoardStr = boardToMongoString(currentState.pieces, game.board)

                // Leitura Segura do Turno
                val remoteTurnStr = remoteState.turn.uppercase()
                val remoteTurn = if (remoteTurnStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK

                // Se houve mudança no tabuleiro OU no turno
                if (remoteBoardStr != localBoardStr || remoteTurn != currentState.currentTurn) {
                    println("REFRESH: Atualizando para turno $remoteTurn")

                    val newPieces = mongoStringToBoard(remoteBoardStr, game.board)

                    // Isto deve obrigar o jogo a recalcular as jogadas válidas para as novas peças
                    val updatedState = ReversiState(
                        pieces = newPieces,
                        currentTurn = remoteTurn,
                        consecutivePasses = 0,
                        board = game.board
                    )

                    // Atualizar tudo
                    updateLocalState(updatedState)
                    updateGameInternal(updatedState)

                    statusMessage = if (remoteTurn == localPlayerColor) "A TUA VEZ!" else "Vez do adversário."
                }
            }
        }
    }

    // Atualiza apenas o estado local do ViewModel
    private fun updateLocalState(newState: ReversiState) {
        currentState = newState
    }

    // Atualiza o estado interno do jogo Reversi
    private fun updateGameInternal(newState: ReversiState) {
        try {
            game.currentState = newState
        } catch (e: Exception) {
            // Se for val, ignoramos (o ViewModel gere o estado visual)
        }
    }

    /**
     * Guarda o estado atual do jogo na pasta local 'game_states'.
     * O nome do ficheiro será "NomeDoJogo_Backup.txt".
     */
    fun saveLocalBackup() {
        try {
            // 1. Definir a pasta (no diretório do projeto)
            val folder = File("game_states")
            if (!folder.exists()) {
                folder.mkdirs() // Cria a pasta se não existir
            }

            // 2. Definir o ficheiro
            val filename = "${gameName}.txt"
            val file = File(folder, filename)

            // 3. Preparar os dados (formato similar ao do MongoDB)
            val boardStr = boardToMongoString(currentState.pieces, game.board)
            val turnStr = currentState.currentTurn.toString()

            val content = """
                Game: $gameName
                Timestamp: ${System.currentTimeMillis()}
                Turn: $turnStr
                Board: $boardStr
            """.trimIndent()

            // 4. Escrever no ficheiro
            file.writeText(content)
            println("BACKUP LOCAL: Jogo guardado em '${file.absolutePath}'")

        } catch (e: Exception) {
            println("ERRO BACKUP: Não foi possível guardar localmente: ${e.message}")
        }
    }

    /**
     * Obtém a peça na célula especificada.
     * @param cell A célula do tabuleiro.
     * @return A peça nessa célula, ou null se estiver vazia.
     */
    fun getPieceAt(cell: Cell): ReversiPiece? = currentState.pieces[cell]
}