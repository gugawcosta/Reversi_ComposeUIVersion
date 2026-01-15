package reversi_ui.screens.lobby

import androidx.compose.foundation.*
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
import reversi.core.Reversi
import reversi.core.ReversiState
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.mongoStringToBoard
import reversi_data.AppConfig
import reversi_data.model.GameState
import reversi_ui.screens.home.sessionUserId

@Composable
fun LobbyScreen(
    onJoinGame: (Reversi, String, ReversiColor) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var gamesList by remember { mutableStateOf<List<GameState>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scaffoldState = rememberScaffoldState()

    // Usa o gameManager genérico para lobby
    val gameManager = AppConfig.activeManager

    fun loadGames() {
        scope.launch {
            isLoading = true
            gamesList = gameManager.getAllGames()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadGames() }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Box(contentAlignment = Alignment.Center) {
                TopAppBar(
                    title = { Spacer(Modifier.fillMaxSize()) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { loadGames() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                        }
                    },
                    backgroundColor = Color(0xFF1B5E20),
                    contentColor = Color.White,
                    elevation = 0.dp
                )
                Text("Lobby", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFF0F260F))
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (gamesList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum jogo encontrado.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(gamesList) { gameItem ->
                        GameLobbyCard(
                            gameState = gameItem,
                            onJoin = {
                                scope.launch {
                                    // Usa o gameManager genérico
                                    val canEnter = gameManager.joinGameAsPlayer2(gameItem.gameName, sessionUserId)

                                    if (!canEnter) {
                                        scaffoldState.snackbarHostState.showSnackbar("Jogo cheio ou inacessível.")
                                        return@launch
                                    }

                                    val size = if (gameItem.boardSize > 0) gameItem.boardSize else 8
                                    val loadedGame = Reversi(ReversiBoard(size, size))
                                    val newPieces = mongoStringToBoard(gameItem.board, loadedGame.board)
                                    val turnStr = gameItem.turn.uppercase()
                                    val turnColor = if (turnStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK
                                    val p1ColorStr = gameItem.p1Color.uppercase()
                                    val creatorColor = if (p1ColorStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK
                                    val myColor = if (gameItem.player1Id == sessionUserId) creatorColor else (if (creatorColor == ReversiColor.BLACK) ReversiColor.WHITE else ReversiColor.BLACK)

                                    try {
                                        val newState = ReversiState(newPieces, turnColor, 0, loadedGame.board)
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

@Composable
fun GameLobbyCard(gameState: GameState, onJoin: () -> Unit) {
    val p1ColorStr = gameState.p1Color.uppercase()
    val creatorIsBlack = !p1ColorStr.contains("WHITE")
    val turnStr = gameState.turn.uppercase()
    val isBlackTurn = !turnStr.contains("WHITE")

    val isBoardFull = remember(gameState.board) {
        !gameState.board.contains('E', ignoreCase = true) && !gameState.board.contains('-', ignoreCase = true)
    }

    val blackCount = gameState.board.count { it == 'B' }
    val whiteCount = gameState.board.count { it == 'W' }

    val resultText = remember(blackCount, whiteCount) {
        when {
            blackCount > whiteCount -> "Pretas ($blackCount)"
            whiteCount > blackCount -> "Brancas ($whiteCount)"
            else -> "Empate ($blackCount-$whiteCount)"
        }
    }

    val cardShape = RoundedCornerShape(16.dp)
    val btnText = if (isBoardFull) "JOGO TERMINADO" else "ENTRAR"
    val btnColor = if (isBoardFull) Color.Gray else Color(0xFF4CAF50)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(cardShape)
            .clickable(enabled = !isBoardFull, onClick = onJoin),
        shape = cardShape,
        color = Color(0xFF1B5E20),
        elevation = 8.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = gameState.gameName,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = if (isBoardFull) Color.LightGray else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(Color(0xFF2E7D32))
                    .border(1.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            ) {
                val size = if (gameState.boardSize > 0) gameState.boardSize else 8
                MiniBoardCanvas(boardString = gameState.board, boardSize = size)

                if (isBoardFull) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Fechado", tint = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Criador:", fontSize = 10.sp, color = Color.LightGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = if (creatorIsBlack) Color.Black else Color.White, modifier = Modifier.size(8.dp)) {}
                        Spacer(Modifier.width(4.dp))
                        Text(if (creatorIsBlack) "Pretas" else "Brancas", fontSize = 11.sp, color = Color.White)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (isBoardFull) {
                        Text("Vencedor:", fontSize = 10.sp, color = Color.LightGray)
                        Text(text = resultText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    } else {
                        Text("Turno:", fontSize = 10.sp, color = Color.LightGray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = if (isBlackTurn) Color.Black else Color.White, modifier = Modifier.size(8.dp)) {}
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBlackTurn) "Pretas" else "Brancas", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            Button(
                onClick = onJoin,
                enabled = !isBoardFull,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = btnColor,
                    disabledBackgroundColor = Color.Black.copy(alpha = 0.2f),
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

@Composable
fun MiniBoardCanvas(boardString: String, boardSize: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
        val cellSize = size.width / boardSize
        val radius = cellSize / 2 * 0.7f

        for (i in 1 until boardSize) {
            val pos = i * cellSize
            drawLine(color = Color.Black.copy(alpha = 0.3f), start = Offset(pos, 0f), end = Offset(pos, size.height), strokeWidth = 1f)
            drawLine(color = Color.Black.copy(alpha = 0.3f), start = Offset(0f, pos), end = Offset(size.width, pos), strokeWidth = 1f)
        }

        boardString.forEachIndexed { index, char ->
            if (index < boardSize * boardSize) {
                val col = index % boardSize
                val row = index / boardSize
                val centerX = col * cellSize + (cellSize / 2)
                val centerY = row * cellSize + (cellSize / 2)

                if (char == 'B') drawCircle(color = Color.Black, radius = radius, center = Offset(centerX, centerY))
                else if (char == 'W') drawCircle(color = Color.White, radius = radius, center = Offset(centerX, centerY))
            }
        }
    }
}