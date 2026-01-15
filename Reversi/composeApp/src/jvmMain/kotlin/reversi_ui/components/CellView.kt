package reversi_ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import reversi.model.ReversiColor

/**
 * Composable que representa uma célula do tabuleiro do jogo Reversi.
 * @param piece Cor da peça na célula (preto, branco ou nulo).
 * @param showHint Indica se deve mostrar as jogadas possíveis.
 * @param hintColor Cor da dica de jogada possível.
 * @param row Linha da célula.
 * @param col Coluna da célula.
 * @param onClick Função a ser chamada quando a célula for clicada.
 */
@Composable
fun CellView(
    piece: ReversiColor?,
    showHint: Boolean,
    hintColor: ReversiColor?,
    row: Int,
    col: Int,
    onClick: (row: Int, col: Int) -> Unit
) {
    val cellSize = 50.dp
    val pieceSize = 40.dp

    val colorHint = when (hintColor) {
        ReversiColor.BLACK -> Color.Black.copy(alpha = 0.5f)
        ReversiColor.WHITE -> Color.White.copy(alpha = 0.5f)
        else -> Color.Yellow.copy(alpha = 0.5f)
    }

    // Animação da aura
    val infiniteTransition = rememberInfiniteTransition()
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Estado para detetar change (flip) entre valores anteriores e atuais
    var prevPiece by remember { mutableStateOf(piece) }
    val currentPiece = rememberUpdatedState(piece)

    // Animatable para rotação em Y (0..180)
    val rotationAnim = remember { Animatable(0f) }
    var isFlipping by remember { mutableStateOf(false) }

    val density = LocalDensity.current.density

    // Dispara animação apenas quando houver flip
    LaunchedEffect(currentPiece.value) {
        val newPiece = currentPiece.value
        val old = prevPiece
        if (old != null && newPiece != null && old != newPiece) {
            isFlipping = true
            rotationAnim.snapTo(0f)
            rotationAnim.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            isFlipping = false
        }
        prevPiece = newPiece
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(Color(0xFF3FA73F), RoundedCornerShape(25.dp)),
        contentAlignment = Alignment.Center
    ) {

        // AURA ANIMADA
        if (showHint && piece == null) {
            Canvas(modifier = Modifier) { }
        }

        // CÍRCULO DAS POSSÍVEIS JOGADAS
        if (showHint && piece == null) {
            Canvas(modifier = Modifier.size(pieceSize)) {
                val size = size
                val radius = size.minDimension / 2
                drawCircle(color = colorHint.copy(alpha = auraAlpha), radius = radius * auraScale)
                drawCircle(color = colorHint, radius = radius)
            }
        }

        // ÁREA CLICÁVEL — APENAS A PEÇA
        Box(
            modifier = Modifier
                .size(pieceSize)
                .clip(RoundedCornerShape(40.dp))
                .clickable { onClick(row, col) },
            contentAlignment = Alignment.Center
        ) {
            if (piece == null && prevPiece == null) return@Box

            val rotation = if (isFlipping) rotationAnim.value else 0f

            val colorToDraw: Color = when {
                isFlipping -> {
                    val old = prevPiece
                    if (rotation <= 90f) {
                        if (old == ReversiColor.BLACK) Color.Black else Color.White
                    } else {
                        if (piece == ReversiColor.BLACK) Color.Black else Color.White
                    }
                }
                else -> {
                    if (piece == ReversiColor.BLACK) Color.Black else Color.White
                }
            }

            Canvas(
                modifier = Modifier
                    .size(pieceSize)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 8 * density
                    }
            ) {
                drawCircle(color = colorToDraw)
            }
        }
    }
}