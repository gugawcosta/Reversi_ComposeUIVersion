package reversi_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import reversi.core.BOARD_HEIGHT
import reversi.core.BOARD_WIDTH
import reversi.core.reversiGetLegalMoves
import reversi.framework.Cell
import kotlin.collections.emptySet

@Composable
fun BoardView(viewModel: GameViewModel) {
    val rows = BOARD_HEIGHT
    val cols = BOARD_WIDTH

    val legalMovesAll: Set<Cell?> = viewModel.currentState
        .reversiGetLegalMoves(viewModel.game.board)
        .map { it.position }
        .toSet()

    val legalMoves = if (viewModel.showTargets) legalMovesAll else emptySet<Cell>()

    var invalidPos by remember { mutableStateOf<Cell?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(Color(0xFF0F7A0F), RoundedCornerShape(32.dp))
            .border(5.dp, Color.DarkGray, RoundedCornerShape(32.dp))
            .padding(10.dp)
    ) {
        for (row in 0 until rows) {
            Row {
                for (col in 0 until cols) {
                    val cell = Cell(row + 1, col + 1)
                    val isLegal = cell in legalMoves

                    Box(
                        modifier = Modifier.size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CellView(
                            piece = viewModel.getPieceAt(cell)?.color,
                            showHint = isLegal,
                            hintColor = viewModel.currentPlayer,
                            row = row + 1,
                            col = col + 1,
                            onClick = { r, c ->
                                val clicked = Cell(r, c)
                                if (clicked !in legalMovesAll) {
                                    invalidPos = clicked
                                } else {
                                    viewModel.onCellClick(clicked)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Suppress("AssignedValueIsNeverRead")
    if (invalidPos != null) {
        AlertDialog(
            onDismissRequest = { invalidPos = null },
            title = { Text("Posição inválida") },
            text = {
                Text(buildAnnotatedString {
                    append("A peça não pode ser jogada nestas coordenadas: ")
                    withStyle(SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                        append("[${invalidPos?.row ?: "?"}; ${invalidPos?.col ?: "?"}]")
                    }
                    append("\n\n")
                    withStyle(SpanStyle(color = Color.Magenta, textDecoration = TextDecoration.Underline)) {
                        append("Dica")
                    }
                    append(": Experimente clicar no botão '")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Mostrar jogadas")
                    }
                    append("' para revelar as células (válidas) onde poderá jogar.")
                })
            },
            confirmButton = {
                Button(onClick = { invalidPos = null }) {
                    Text("OK")
                }
            }
        )
    }
}

