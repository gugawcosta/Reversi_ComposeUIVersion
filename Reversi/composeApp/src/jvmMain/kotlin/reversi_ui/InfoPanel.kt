package reversi_ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import reversi.model.ReversiColor

@Composable
fun InfoPanel(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.width(180.dp).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // só mostra o turno se o jogo não terminou
        if (!viewModel.gameOver) {
            Text(
                text = "Turno: ${if (viewModel.currentPlayer == ReversiColor.BLACK) "Pretas" else "Brancas"}",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 23.sp,
                color = if (viewModel.currentPlayer == ReversiColor.BLACK) Color.Black else Color.Gray
            )
            Spacer(Modifier.height(20.dp))
        } else {
            // mantém espaçamento consistente quando oculto
            Spacer(Modifier.height(20.dp))
        }

        Text(
            text = "Pretas: ${viewModel.blackCount}",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            color = Color.Black
        )
        Text(
            text = "Brancas: ${viewModel.whiteCount}",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.toggleTargets() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF2F2F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (viewModel.showTargets) "Esconder jogadas" else "Mostrar jogadas"
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // botão desativado quando o jogo acabou
        Button(
            onClick = { if (!viewModel.gameOver) viewModel.passTurn() },
            enabled = !viewModel.gameOver && viewModel.currentState.legalMoves.isEmpty(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF2F2F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Passar a Jogada")
            }
        }

        Spacer(Modifier.height(20.dp))

        if (viewModel.gameOver) {
            val label = when {
                viewModel.blackCount > viewModel.whiteCount -> "Vitória das Pretas!"
                viewModel.whiteCount > viewModel.blackCount -> "Vitória das Brancas!"
                else -> "Empate!"
            }

            val outlineColor = Color.Black.copy(alpha = 0.72f)
            val mainStyle = TextStyle(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFE63946), Color(0xFF8B0000))
                ),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.35f),
                    offset = Offset(2f, 2f),
                    blurRadius = 6f
                )
            )
            val outlineStyle = TextStyle(
                color = outlineColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(label, modifier = Modifier.offset(x = (-1).dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = (-1).dp, y = 0.dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = (-1).dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = 0.dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = 0.dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = 1.dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = 1.dp, y = 0.dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(label, modifier = Modifier.offset(x = 1.dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)

                Text(
                    label,
                    style = mainStyle,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

