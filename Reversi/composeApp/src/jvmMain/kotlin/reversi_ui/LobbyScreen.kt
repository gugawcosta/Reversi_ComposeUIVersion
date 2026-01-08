package reversi_ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import mongodb.GameState
import mongodb.MongoGameManager
import reversi.core.Reversi
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.mongoStringToBoard

/**
 * Ecrã do Lobby Online.
 * Mostra a lista de jogos disponíveis para entrar.
 *
 * @param onJoinGame Função chamada quando o utilizador entra num jogo.
 * Recebe o jogo carregado, o nome do jogo e a cor do jogador.
 * @param onBack Função chamada quando o utilizador quer voltar ao ecrã anterior.
 */
@Composable
fun LobbyScreen(
    onJoinGame: (Reversi, String, ReversiColor) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var gamesList by remember { mutableStateOf<List<GameState>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Função para carregar jogos
    fun loadGames() {
        scope.launch {
            isLoading = true
            gamesList = MongoGameManager.getAllGames()
            isLoading = false
        }
    }

    // Carregar ao entrar
    LaunchedEffect(Unit) {
        loadGames()
    }

    // UI do Lobby
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F260F))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }

                Text(
                    text = "Lobby",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(onClick = { loadGames() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = Color.White)
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (gamesList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum jogo encontrado.", color = Color.Gray)
                }
            } else {
                // Lista de Jogos em Grelha
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp), // Responsivo
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(gamesList) { gameItem ->
                        GameLobbyCard(
                            gameState = gameItem,
                            onJoin = {
                                scope.launch {
                                    // Lógica de Join
                                    val loadedGame = Reversi(ReversiBoard(8, 8))
                                    val newPieces = mongoStringToBoard(gameItem.board, loadedGame.board)

                                    val turnStr = gameItem.turn.uppercase()
                                    val turnColor = if (turnStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK

                                    val p1ColorStr = gameItem.p1Color.uppercase()
                                    val creatorColor = if (p1ColorStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK

                                    // A minha cor é a oposta do criador
                                    val myColor = if (creatorColor == ReversiColor.BLACK) ReversiColor.WHITE else ReversiColor.BLACK

                                    // Atualizar estado
                                    try {
                                        val newState = reversi.core.ReversiState(
                                            pieces = newPieces,
                                            currentTurn = turnColor,
                                            consecutivePasses = 0,
                                            board = loadedGame.board
                                        )
                                        loadedGame.currentState = newState
                                    } catch (e: Exception) { e.printStackTrace() }

                                    onJoinGame(loadedGame, gameItem.gameName, myColor)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card individual para cada jogo no Lobby.
 * Mostra o nome do jogo, mini tabuleiro, criador, turno e botão de entrar.
 * @param gameState Estado do jogo a mostrar.
 * @param onJoin Função chamada quando o utilizador clica em "Entrar".
 */
@Composable
fun GameLobbyCard(
    gameState: GameState,
    onJoin: () -> Unit
) {
    // Determinar cores e lógica básica
    val p1ColorStr = gameState.p1Color.uppercase()
    val creatorIsBlack = !p1ColorStr.contains("WHITE")
    val turnStr = gameState.turn.uppercase()
    val isBlackTurn = !turnStr.contains("WHITE")

    // Lógica do fim do jogo
    // 1. Verificar se acabou (tabuleiro cheio ou sem vazios)
    val isBoardFull = remember(gameState.board) {
        !gameState.board.contains('E', ignoreCase = true) &&
                !gameState.board.contains('-', ignoreCase = true)
    }

    // 2. Calcular Vencedor
    val blackCount = gameState.board.count { it == 'B' }
    val whiteCount = gameState.board.count { it == 'W' }

    val resultText = remember(blackCount, whiteCount) {
        when {
            blackCount > whiteCount -> "Pretas ($blackCount)"
            whiteCount > blackCount -> "Brancas ($whiteCount)"
            else -> "Empate ($blackCount-$whiteCount)"
        }
    }

    // Configuração Visual
    val cardShape = RoundedCornerShape(16.dp)

    // Texto e cor do botão
    val btnText = if (isBoardFull) "JOGO TERMINADO" else "ENTRAR"
    val btnColor = if (isBoardFull) Color.Gray else Color(0xFF4CAF50)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(cardShape)
            // Clique bloqueado se o jogo acabou
            .clickable(enabled = !isBoardFull, onClick = onJoin),
        shape = cardShape,
        color = Color(0xFF1B5E20),
        elevation = 8.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Título do Jogo
            Text(
                text = gameState.gameName,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = if (isBoardFull) Color.LightGray else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 2. Mini Tabuleiro
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(Color(0xFF2E7D32))
                    .border(1.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            ) {
                MiniBoardCanvas(boardString = gameState.board)

                // Overlay de Cadeado
                if (isBoardFull) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Fechado",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // 3. Informações do Jogo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Esquerda: Criador
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Criador:", fontSize = 10.sp, color = Color.LightGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (creatorIsBlack) Color.Black else Color.White,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(Modifier.width(4.dp))
                        Text(if (creatorIsBlack) "Pretas" else "Brancas", fontSize = 11.sp, color = Color.White)
                    }
                }

                // Direita: Turno OU Vencedor
                Column(horizontalAlignment = Alignment.End) {
                    if (isBoardFull) {
                        // Mostra o Vencedor
                        Text("Vencedor:", fontSize = 10.sp, color = Color.LightGray)
                        Text(
                            text = resultText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700) // Destacar a vitória
                        )
                    } else {
                        // Mostra o Turno normalmente
                        Text("Turno:", fontSize = 10.sp, color = Color.LightGray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isBlackTurn) Color.Black else Color.White,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBlackTurn) "Pretas" else "Brancas", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // 4. Botão Entrar
            Button(
                onClick = onJoin,
                enabled = !isBoardFull,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = btnColor,
                    disabledBackgroundColor = Color.Black.copy(alpha = 0.2f), // Botão escuro, se desativado
                    disabledContentColor = Color.LightGray
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(32.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp)
            ) {
                Text(btnText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * Desenha um tabuleiro de Reversi miniatura usando Canvas.
 * Lê a 'String' "----------------WB----------------" do MongoDB.
 */
@Composable
fun MiniBoardCanvas(boardString: String) {
    Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
        val cellSize = size.width / 8
        val radius = cellSize / 2 * 0.7f // 70% do tamanho da célula

        // Desenhar Grelha
        for (i in 1..7) {
            val pos = i * cellSize
            // Linhas Verticais
            drawLine(
                color = Color.Black.copy(alpha = 0.3f),
                start = Offset(pos, 0f),
                end = Offset(pos, size.height),
                strokeWidth = 1f
            )
            // Linhas Horizontais
            drawLine(
                color = Color.Black.copy(alpha = 0.3f),
                start = Offset(0f, pos),
                end = Offset(size.width, pos),
                strokeWidth = 1f
            )
        }

        // Desenhar Peças
        // O boardString tem 64 caracteres.
        // O index vai de 0 a 63.
        // x = index % 8, y = index / 8
        // TODO: Garantir que o boardString irá ter sempre o tamanho do tabuleiro que foi definido (ex.: 4x4, 16x16)
        boardString.forEachIndexed { index, char ->
            if (index < 64) {
                val col = index % 8
                val row = index / 8

                val centerX = col * cellSize + (cellSize / 2)
                val centerY = row * cellSize + (cellSize / 2)

                if (char == 'B') {
                    drawCircle(
                        color = Color.Black,
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )
                } else if (char == 'W') {
                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )
                }
            }
        }
    }
}