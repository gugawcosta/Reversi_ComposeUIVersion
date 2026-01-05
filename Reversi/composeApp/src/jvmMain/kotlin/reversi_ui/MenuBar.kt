package reversi_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuBar(
    onNewGame: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fileMenuOpen = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White.copy(alpha = 0.6f))
            .border(
                width = 2.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        // padding aumenta a área da "barra" em todos os lados (esq/dir/cima/baixo)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                // .padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
        ) {
            val menuShape = RoundedCornerShape(
                topStart = 6.dp,
                bottomStart = 6.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            )

            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(Color.Transparent, menuShape)
                    .clip(menuShape)
                    .clickable { fileMenuOpen.value = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Arquivo",
                    fontSize = 16.sp, // corrige o erro: usar 'sp' em vez de 'dp'
//                    modifier = Modifier
//                        .wrapContentWidth()
//                        .wrapContentHeight()
//                        .clickable { fileMenuOpen.value = true }
                )

                DropdownMenu(
                    expanded = fileMenuOpen.value,
                    onDismissRequest = { fileMenuOpen.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Novo jogo") },
                        onClick = {
                            fileMenuOpen.value = false
                            onNewGame()
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Sair") },
                        onClick = {
                            fileMenuOpen.value = false
                            onExit()
                        }
                    )
                }
            }
        }
    }
}

