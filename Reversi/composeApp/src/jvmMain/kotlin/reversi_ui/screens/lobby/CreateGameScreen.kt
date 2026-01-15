package reversi_ui.screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import reversi.model.ReversiColor
import reversi.core.Reversi
import reversi.model.ReversiBoard
import reversi_data.mongodb.MongoGameManager
import kotlin.math.roundToInt

/**
 * Ecrã para criar um jogo.
 * @param myUserId ID único do utilizador atual (Player 1).
 * @param onConfirm Função chamada quando o user confirma a criação do jogo.
 * @param onDismiss Função chamada quando o user cancela a criação do jogo.
 */
@Composable
fun CreateGameScreen(
    onConfirm: (String, ReversiColor, Int) -> Unit,
    onDismiss: () -> Unit,
    myUserId: String
) {
    val gameName = remember { mutableStateOf("") }
    val selectedColor = remember { mutableStateOf(ReversiColor.BLACK) }

    // Slider para tamanho do tabuleiro (4, 6, 8, 10, 12)
    var boardSize by remember { mutableStateOf(8f) }

    val isWorking = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isWorking.value) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 12.dp,
            backgroundColor = Color(0xFFF9F9F9)
        ) {
            Column(
                modifier = Modifier.width(340.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabeçalho
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CRIAR NOVO JOGO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Conteúdo
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Nome da Sala:", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gameName.value,
                        onValueChange = { gameName.value = it; errorMessage.value = null },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color(0xFF1B5E20),
                            textColor = Color.Black
                        ),
                        placeholder = { Text("Vazio = Jogo Local", color = Color.Gray, fontSize = 13.sp) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto mostra sempre o valor par atual
                    Text("Tamanho: ${boardSize.toInt()}x${boardSize.toInt()}", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                    // Estratégia: O Slider trabalha com índices 0, 1, 2, 3, 4.
                    // Nós convertemos esses índices para 4, 6, 8, 10, 12.
                    // Isto impede matematicamente que apareçam números ímpares intermédios.
                    Slider(
                        value = (boardSize - 4) / 2, // Converte valor real (ex: 8) para índice (ex: 2)
                        onValueChange = { newIndex ->
                            // Arredonda para garantir que é um passo inteiro e converte de volta para o tamanho
                            // Ex: Índice 2 -> 4 + (2 * 2) = 8
                            val index = newIndex.roundToInt()
                            boardSize = 4f + (index * 2)
                        },
                        valueRange = 0f..4f, // 5 posições (0, 1, 2, 3, 4)
                        steps = 3, // Passos intermédios (1, 2, 3)
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF2E7D32),
                            activeTrackColor = Color(0xFF2E7D32),
                            inactiveTrackColor = Color(0xFFC8E6C9)
                        )
                    )
                    // ------------------------

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("A tua cor:", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedColor.value == ReversiColor.BLACK,
                            onClick = { selectedColor.value = ReversiColor.BLACK },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                        )
                        Text("Pretas", fontSize = 14.sp)

                        Spacer(modifier = Modifier.width(24.dp))

                        RadioButton(
                            selected = selectedColor.value == ReversiColor.WHITE,
                            onClick = { selectedColor.value = ReversiColor.WHITE },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2E7D32)) // Ajustei para Verde para combinar
                        )
                        Text("Brancas", fontSize = 14.sp)
                    }

                    if (errorMessage.value != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage.value!!,
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isWorking.value,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                if (isWorking.value) return@Button
                                isWorking.value = true
                                errorMessage.value = null

                                val nameTrim = gameName.value.trim()
                                val sizeInt = boardSize.toInt()

                                // Jogo Local
                                if (nameTrim.isEmpty()) {
                                    onConfirm(nameTrim, selectedColor.value, sizeInt)
                                    return@Button
                                }

                                // Jogo Online
                                scope.launch {
                                    try {
                                        val newGame = Reversi(ReversiBoard(sizeInt, sizeInt), startingColor = selectedColor.value)
                                        val success = MongoGameManager.createNewGame(nameTrim, newGame, selectedColor.value, myUserId, sizeInt)

                                        if (success) {
                                            onConfirm(nameTrim, selectedColor.value, sizeInt)
                                        } else {
                                            errorMessage.value = "Nome já existe."
                                            isWorking.value = false
                                        }
                                    } catch (e: Exception) {
                                        errorMessage.value = "Erro de conexão."
                                        isWorking.value = false
                                    }
                                }
                            },
                            enabled = !isWorking.value,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isWorking.value) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Criar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}