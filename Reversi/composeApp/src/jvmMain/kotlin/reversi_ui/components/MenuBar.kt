package reversi_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de Menu superior da aplicação.
 * Contém os menus: Game e Options (simplificados).
 * @param onRefresh Ação ao atualizar o estado do jogo.
 * @param onExit Ação ao sair do jogo.
 * @param onAutoRefreshToggle Ação ao alternar o auto-refresh.
 * @param isAutoRefreshOn Estado atual do auto-refresh.
 * @param canRefresh Controla se o botão Refresh está ativo.
 * @param modifier Modificador para estilização adicional.
 */
@Composable
fun MenuBar(
    // Ações do Menu Game
    onRefresh: () -> Unit,
    onExit: () -> Unit,

    // Ações do Menu Options
    onAutoRefreshToggle: () -> Unit,
    isAutoRefreshOn: Boolean,

    // Estado para habilitar Refresh (Auto-refresh OFF e Turno do Adversário)
    canRefresh: Boolean = true,

    modifier: Modifier = Modifier
) {
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
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            // Menu: Game
            MenuDropdown(label = "Game") { closeMenu ->
                DropdownMenuItem(
                    text = { Text("Refresh") },
                    onClick = { closeMenu(); onRefresh() },
                    enabled = canRefresh
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Exit") },
                    onClick = { closeMenu(); onExit() }
                )
            }

            // Menu: Options
            MenuDropdown(label = "Options") { closeMenu ->
                DropdownMenuItem(
                    text = {
                        val check = if (isAutoRefreshOn) "✔ " else ""
                        Text("${check}Auto-refresh")
                    },
                    onClick = { closeMenu(); onAutoRefreshToggle() }
                )
            }
        }
    }
}

/**
 * Função auxiliar para criar os botões do menu e evitar repetição de código.
 */
@Composable
fun MenuDropdown(
    label: String,
    content: @Composable (closeMenu: () -> Unit) -> Unit
) {
    val isOpen = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clickable { isOpen.value = true }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black
        )

        DropdownMenu(
            expanded = isOpen.value,
            onDismissRequest = { isOpen.value = false }
        ) {
            content { isOpen.value = false }
        }
    }
}