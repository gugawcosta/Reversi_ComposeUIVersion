package org.example.reversi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EnterGameDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val gameName = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Entrar num jogo") },
        text = {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text("Introduza o nome do jogo:")
                OutlinedTextField(
                    value = gameName.value,
                    onValueChange = { gameName.value = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp), // altura mínima padrão para não cortar texto
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFE8F5E9),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color(0xFF81C784),
                        cursorColor = Color(0xFF1B5E20)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(gameName.value.trim()) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
