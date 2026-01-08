package reversi_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import reversi.model.ReversiColor

/**
 * Painel de informação lateral do jogo.
 * Mostra o turno atual, pontuação, botões de ação, etc.
 * @param viewModel ViewModel do jogo.
 * @return Composable do painel de informação.
 */
@Composable
fun InfoPanel(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.width(180.dp).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (viewModel.isMultiplayer) {
            val turnBlack = viewModel.localPlayerColor == ReversiColor.BLACK

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (turnBlack) Color.Black else Color.White)
                    .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "JOGAS COM AS:",
                        fontSize = 12.sp,
                        color = if (turnBlack) Color.White else Color.Black,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = if (turnBlack) "PRETAS" else "BRANCAS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (turnBlack) Color.White else Color.Black
                    )
                }
            }
        }

        // Turno Atual (Escondido se o jogo acabou)
        if (!viewModel.gameOver) {
            Text(
                text = "Turno Atual:",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Text(
                text = if (viewModel.currentPlayer == ReversiColor.BLACK) "Pretas" else "Brancas",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 26.sp, // Aumentei um pouco para destaque
                color = if (viewModel.currentPlayer == ReversiColor.BLACK) Color.Black else Color.DarkGray
            )
            Spacer(Modifier.height(20.dp))
        } else {
            Spacer(Modifier.height(20.dp))
        }

        // Contagem de Peças/ Pontuação
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Brancas -> ", color = Color.Gray, fontSize = 20.sp)
            Text(
                text = "${viewModel.blackCount}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Gray
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pretas -> ", color = Color.Black, fontSize = 20.sp)
            Text(
                text = "${viewModel.whiteCount}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )
        }

        Spacer(Modifier.height(30.dp))

        // Botão de Mostrar/Esconder Jogadas Válidas fica desativado se não for o meu turno no multiplayer
        val canShowHints = !viewModel.isMultiplayer || (viewModel.currentPlayer == viewModel.localPlayerColor)

        Button(
            onClick = { viewModel.toggleTargets() },
            enabled = canShowHints,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF2F2F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.showTargets) "Esconder Jogadas" else "Mostrar Jogadas",
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        val canPass = if (viewModel.isMultiplayer) {
            // Multiplayer: A minha vez E sem jogadas válidas
            !viewModel.gameOver &&
                    viewModel.currentPlayer == viewModel.localPlayerColor &&
                    viewModel.currentState.legalMoves.isEmpty()
        } else {
            // Singleplayer: Sem jogadas válidas para quem quer que seja o turno atual
            !viewModel.gameOver && viewModel.currentState.legalMoves.isEmpty()
        }

        Button(
            onClick = { if (!viewModel.gameOver) viewModel.passTurn() },
            enabled = canPass,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF2F2F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Passar a Jogada")
        }

        Spacer(Modifier.height(20.dp))

        if (viewModel.gameOver) {
            val label = when {
                viewModel.blackCount > viewModel.whiteCount -> "Vitória das Pretas!"
                viewModel.whiteCount > viewModel.blackCount -> "Vitória das Brancas!"
                else -> "Empate!"
            }

            // Estilo do texto de vitória
            val mainStyle = TextStyle(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFE63946), Color(0xFF8B0000))
                ),
                fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(2f, 2f), blurRadius = 6f)
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    label,
                    style = mainStyle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}