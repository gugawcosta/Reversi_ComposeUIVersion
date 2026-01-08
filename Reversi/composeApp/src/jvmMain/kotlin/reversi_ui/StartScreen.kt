package reversi_ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import reversi.composeapp.generated.resources.Res
import reversi.composeapp.generated.resources.reversi
import mongodb.MongoGameManager
import reversi.core.Reversi
import reversi.core.ReversiState
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi.model.mongoStringToBoard

/**
 * Ecrã inicial do jogo, com opções para criar ou entrar num jogo.
 * @param onGameStart Função chamada quando um jogo é iniciado.
 * Recebe o jogo, o nome do jogo e a cor do jogador.
 * @param onResolution Função chamada para abrir o ecrã de resolução.
 * @param onOpenLobby Função chamada para abrir o lobby online.
 */
@Composable
fun StartScreen(
    onGameStart: (Reversi, String, ReversiColor) -> Unit,
    onResolution: () -> Unit,
    onOpenLobby: () -> Unit
) {
    // Scope para corrotinas
    val scope = rememberCoroutineScope()

    // Estados para mostrar os diálogos
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    // Estado para mensagem de erro no Join
    var joinErrorMessage by remember { mutableStateOf<String?>(null) }

    // Dialog de criar jogo
    if (showCreateDialog) {
        CreateGameScreen(
            onDismiss = { showCreateDialog = false },
            onConfirm = { gameName, selectedColor ->
                // Aqui só precisamos de iniciar a UI localmente.
                showCreateDialog = false // Fecha o dialog
                val newGame = Reversi(ReversiBoard(8, 8), startingColor = selectedColor)
                onGameStart(newGame, gameName, selectedColor) // Muda de ecrã
            }
        )
    }

    // Dialog de entrar num jogo
    if (showJoinDialog) {
        EnterGameDialog(
            errorMessage = joinErrorMessage,
            onInteraction = { joinErrorMessage = null },
            onDismiss = {
                showJoinDialog = false
                joinErrorMessage = null
            },
            onConfirm = { gameNameInput ->
                scope.launch {
                    val cleanName = gameNameInput.trim()

                    if (cleanName.isEmpty()) {
                        joinErrorMessage = "Introduza um nome."
                        return@launch
                    }

                    val gameState = MongoGameManager.loadGameState(cleanName)

                    if (gameState != null) {
                        val turnStr = gameState.turn.uppercase()
                        val turnColor = if (turnStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK
                        val p1ColorStr = gameState.p1Color.uppercase()
                        val creatorColor = if (p1ColorStr.contains("WHITE")) ReversiColor.WHITE else ReversiColor.BLACK

                        // Quem entra é a cor oposta
                        val myColor = if (creatorColor == ReversiColor.BLACK) ReversiColor.WHITE else ReversiColor.BLACK

                        // Reconstruir o jogo
                        val loadedGame = Reversi(ReversiBoard(8, 8), startingColor = turnColor)
                        val newPieces = mongoStringToBoard(gameState.board, loadedGame.board)

                        try {
                            val newState = ReversiState(
                                pieces = newPieces,
                                currentTurn = turnColor,
                                consecutivePasses = 0,
                                board = loadedGame.board
                            )
                            loadedGame.currentState = newState
                        } catch (e: Exception) { }

                        joinErrorMessage = null
                        showJoinDialog = false
                        onGameStart(loadedGame, cleanName, myColor)
                    } else {
                        joinErrorMessage = "Jogo não encontrado."
                    }
                }
            }
        )
    }

    // UI Principal com animação de fundo
    val transition = rememberInfiniteTransition()
    val animProgress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    var boxWidthPx by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F260F)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.clip(RoundedCornerShape(24.dp)).padding(16.dp).fillMaxWidth(0.86f).height(420.dp),
            elevation = 8.dp,
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .onSizeChanged { boxWidthPx = it.width }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF49A64C), Color(0xFF45A049), Color(0xFF388E3C), Color(0xFF2E7D32), Color(0xFF1B5E20)),
                            start = Offset(x = if (boxWidthPx == 0) 0f else animProgress * boxWidthPx, y = 0f),
                            end = Offset(x = if (boxWidthPx == 0) boxWidthPx.toFloat() else (animProgress * boxWidthPx + boxWidthPx), y = 0f)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(28.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.reversi),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp).align(Alignment.TopStart).offset((-16).dp, (-16).dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp, alignment = Alignment.CenterVertically)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), contentAlignment = Alignment.Center) {
                        val label = "Reversi"
                        val outlineColor = Color.Black.copy(alpha = 0.72f)
                        val mainStyle = TextStyle(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF9BE49A), Color(0xFF49A64C), Color(0xFF2F7F33))),
                            fontSize = 50.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif, letterSpacing = 1.2.sp,
                            shadow = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(3f, 3f), blurRadius = 8f)
                        )
                        val outlineStyle = TextStyle(color = outlineColor, fontSize = 50.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif, letterSpacing = 1.2.sp)

                        for(dx in -1..1) for(dy in -1..1) if(dx!=0||dy!=0)
                            Text(label, modifier=Modifier.offset(dx.dp, dy.dp), style=outlineStyle)
                        Text(label, style = mainStyle, textAlign = TextAlign.Center)
                    }

                    AccentButton(text = "Criar jogo", onClick = { showCreateDialog = true }, modifier = Modifier.fillMaxWidth(0.3f))
                    AccentButton(text = "Entrar num jogo", onClick = { showJoinDialog = true; joinErrorMessage = null }, modifier = Modifier.fillMaxWidth(0.3f))
                    AccentButton(text = "Lobby Online", onClick = onOpenLobby, modifier = Modifier.fillMaxWidth(0.3f))
                    AccentButton(text = "Resolução", onClick = onResolution, modifier = Modifier.fillMaxWidth(0.2f))
                }
            }
        }
    }
}

/**
 * Botão com estilo "accent" personalizado.
 * @param text Texto do botão.
 * @param onClick Função chamada ao clicar no botão.
 * @param modifier Modificador do Compose.
 * @param background Cor de fundo do botão.
 * @param borderColor Cor da borda do botão.
 * @param shape Forma do botão.
 * @param contentPadding Espaçamento interno do botão.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.06f),
    borderColor: Color = Color.Black.copy(alpha = 0.75f),
    shape: Shape = RoundedCornerShape(32.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
) {
    var hovered by remember { mutableStateOf(false) }
    val bg = if (hovered) background.copy(alpha = 0.12f) else background
    val elevation = if (hovered) 6.dp else 0.dp

    Box(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { hovered = true }
            .onPointerEvent(PointerEventType.Exit) { hovered = false }
            .shadow(elevation, shape)
            .clip(shape)
            .background(bg, shape)
            .border(BorderStroke(2.dp, borderColor), shape)
            .clickable(onClick = onClick, indication = null, interactionSource = remember { MutableInteractionSource() })
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        val outlineColor = Color.Black.copy(alpha = 0.72f)
        val mainStyle = TextStyle(
            brush = Brush.horizontalGradient(listOf(Color(0xFF9BE49A), Color(0xFF49A64C), Color(0xFF2F7F33))),
            fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif, letterSpacing = 0.4.sp,
            shadow = Shadow(color = Color.Black.copy(alpha = 0.25f), offset = Offset(1f, 1f), blurRadius = 6f)
        )
        val outlineStyle = TextStyle(color = outlineColor, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif, letterSpacing = 0.4.sp)
        for(dx in -1..1) for(dy in -1..1) if(dx!=0||dy!=0) Text(text, modifier=Modifier.offset(dx.dp, dy.dp), style=outlineStyle)
        Text(text, style = mainStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}