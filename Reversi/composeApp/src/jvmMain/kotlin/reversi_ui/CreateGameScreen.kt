package reversi_ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import reversi.model.ReversiColor
import reversi.core.Reversi

@Composable
fun CreateGameScreen(
    onConfirm: (String, ReversiColor) -> Unit,
    onDismiss: () -> Unit
) {
    val gameName = remember { mutableStateOf("") }
    val selectedColor = remember { mutableStateOf(ReversiColor.WHITE) }
    val isWorking = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFE8F5E9),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color(0xFF81C784),
                        cursorColor = Color(0xFF1B5E20)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Escolha a cor de peças que deseja jogar:")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Opção Pretas
                    ColorOption(
                        label = "Pretas",
                        swatchColor = Color(0xFF0D0D0D),
                        isSelected = selectedColor.value == ReversiColor.BLACK,
                        onSelect = { selectedColor.value = ReversiColor.BLACK }
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    // Opção Brancas
                    ColorOption(
                        label = "Brancas",
                        swatchColor = Color(0xFFEEEEEE),
                        isSelected = selectedColor.value == ReversiColor.WHITE,
                        onSelect = { selectedColor.value = ReversiColor.WHITE }
                    )
                }

                errorMessage.value?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = msg, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isWorking.value) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(
                    onClick = {
                        val nameTrim = gameName.value.trim()
                        // Se não houver nome, cria apenas o jogo local (manter comportamento opcional)
                        if (nameTrim.isEmpty()) {
                            onConfirm(nameTrim, selectedColor.value)
                            return@TextButton
                        }

                        // Caso com nome: tentativa de criar ficheiro
                        scope.launch {
                            isWorking.value = true
                            errorMessage.value = null
                            try {
                                val newGame = Reversi(startingColor = selectedColor.value)
                                val success = GameFileManager.createNewGameFile(nameTrim, newGame, selectedColor.value)
                                if (success)
                                    onConfirm(nameTrim, selectedColor.value)
                                else
                                    errorMessage.value = "Este jogo já existe: $nameTrim"
                            }
                            catch (_: Throwable) {
                                errorMessage.value = "Erro ao guardar o jogo"
                            }
                            finally {
                                isWorking.value = false
                            }
                        }
                    },
                    enabled = !isWorking.value
                ) {
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

@Composable
private fun ColorOption(
    label: String,
    swatchColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // animação suave para a borda quando selecionado
    val borderWidth = animateDpAsState(targetValue = if (isSelected) 3.dp else 1.dp)
    // val elevation = animateDpAsState(targetValue = if (isSelected) 8.dp else 2.dp)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)) // apenas a área clicável com cantos arredondados
            .clickable { onSelect() }
            .padding(vertical = 4.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                //.shadow(elevation.value, CircleShape)
                .background(swatchColor, CircleShape)
                .then(
                    Modifier.border(
                        BorderStroke(borderWidth.value, if (isSelected) Color(0xFFFFD54F) else Color(0xFF9E9E9E)),
                        CircleShape
                    )
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Texto com fonte diferente e destaque quando selecionado
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isSelected) 16.sp else 14.sp,
            color = Color(0xFF263238)
        )
    }
}
