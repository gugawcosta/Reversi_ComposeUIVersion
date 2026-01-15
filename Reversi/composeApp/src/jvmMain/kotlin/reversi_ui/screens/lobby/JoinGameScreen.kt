package reversi_ui.screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Diálogo para o user entrar num jogo multiplayer existente.
 * @param onConfirm Função chamada quando o user confirma o nome do jogo.
 * Recebe o nome do jogo.
 * @param onDismiss Função chamada quando o user cancela a entrada.
 * @param errorMessage Mensagem de erro a exibir (se houver).
 * @param onInteraction Função chamada quando o user interage (escreve) no
 * campo de texto, para limpar erros.
 */
@Composable
fun EnterGameDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null, // Recebe a mensagem de erro
    onInteraction: () -> Unit = {} // Para limpar o erro quando o user escreve
) {
    val gameName = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 12.dp,
            backgroundColor = Color(0xFFF9F9F9)
        ) {
            Column(
                modifier = Modifier.width(320.dp),
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
                        text = "ENTRAR NUM JOGO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Conteúdo
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Introduza o nome do jogo:",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gameName.value,
                        onValueChange = {
                            gameName.value = it
                            onInteraction() // Limpa o erro ao escrever
                        },
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
                        placeholder = { Text("Ex: Jogo1", color = Color.Gray) }
                    )

                    // Exibir Erro
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botões Entrar / Cancelar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { onConfirm(gameName.value.trim()) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Entrar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}