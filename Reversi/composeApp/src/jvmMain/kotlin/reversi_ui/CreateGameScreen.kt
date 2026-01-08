package reversi_ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import reversi.model.ReversiColor
import reversi.core.Reversi
import reversi.model.ReversiBoard
import mongodb.MongoGameManager

/**
 * Ecrã para criar um jogo (local ou online).
 * @param onConfirm Função chamada quando o jogo é criado com sucesso.
 * Recebe o nome do jogo e a cor escolhida.
 * @param onDismiss Função chamada quando o utilizador cancela a criação.
 */
@Composable
fun CreateGameScreen(
    onConfirm: (String, ReversiColor) -> Unit,
    onDismiss: () -> Unit
) {
    val gameName = remember { mutableStateOf("") }
    val selectedColor = remember { mutableStateOf(ReversiColor.BLACK) }

    // Estados de controlo
    val isWorking = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Proteção contra cliques rápidos (Debounce)
    var lastClickTime by remember { mutableStateOf(0L) }

    // Scope para corrotinas
    val scope = rememberCoroutineScope()

    /**
     * Lógica de criação do jogo:
     * 1. Se o nome do jogo estiver vazio, cria um jogo local.
     * 2. Se o nome do jogo não estiver vazio, tenta criar um jogo online
     * 3. Se o nome já existir, mostra erro.
     */

    AlertDialog(
        onDismissRequest = {
            if (!isWorking.value) onDismiss()
        },
        title = { Text("Criar um jogo") },
        text = {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text("Introduza o nome do jogo (opcional):")
                OutlinedTextField(
                    value = gameName.value,
                    onValueChange = {
                        gameName.value = it
                        errorMessage.value = null
                    },
                    singleLine = true,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    label = { Text("Nome da Sala") },
                    placeholder = { Text("Vazio = Jogo Local") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Escolha a cor de peças:")
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedColor.value == ReversiColor.BLACK,
                        onClick = { selectedColor.value = ReversiColor.BLACK }
                    )
                    Text("Pretas", modifier = Modifier.padding(start = 4.dp))

                    Spacer(modifier = Modifier.width(24.dp))

                    RadioButton(
                        selected = selectedColor.value == ReversiColor.WHITE,
                        onClick = { selectedColor.value = ReversiColor.WHITE }
                    )
                    Text("Brancas", modifier = Modifier.padding(start = 4.dp))
                }

                if (errorMessage.value != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage.value!!, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Ignora cliques com menos de 1 segundo de intervalo, para proteção
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < 1000) return@Button
                    lastClickTime = currentTime

                    val nameTrim = gameName.value.trim()

                    // Proteção Visual
                    if (isWorking.value) return@Button

                    // Bloqueia UI
                    isWorking.value = true
                    errorMessage.value = null

                    // Caso A: Jogo Local (Não usa Mongo)
                    if (nameTrim.isEmpty()) {
                        onConfirm(nameTrim, selectedColor.value)
                        return@Button
                    }

                    // Caso B: Jogo Online (Cria no Mongo qqui)
                    scope.launch {
                        try {
                            val newGame = Reversi(ReversiBoard(8, 8), startingColor = selectedColor.value)
                            val success = MongoGameManager.createNewGame(nameTrim, newGame, selectedColor.value)

                            if (success) {
                                onConfirm(nameTrim, selectedColor.value)
                            } else {
                                errorMessage.value = "Erro: Esse nome já existe."
                                isWorking.value = false // Desbloqueia para tentar outro nome
                            }
                        } catch (e: Exception) {
                            errorMessage.value = "Erro de conexão."
                            isWorking.value = false
                        }
                    }
                },
                enabled = !isWorking.value
            ) {
                if (isWorking.value) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("OK")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isWorking.value) {
                Text("Cancelar")
            }
        }
    )
}