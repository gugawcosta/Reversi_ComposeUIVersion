package reversi_ui

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
 * Contém os menus: Game, Play, Options.
 * @param onNewGame Ação ao iniciar um novo jogo.
 * @param onJoinGame Ação ao entrar num jogo multiplayer.
 * @param onRefresh Ação ao atualizar o estado do jogo.
 * @param onExit Ação ao sair do jogo.
 * @param onPass Ação ao passar o turno.
 * @param canPass Controla se o botão Pass está ativo.
 * @param onShowTargetsToggle Ação ao alternar a exibição de alvos.
 * @param isShowTargetsOn Estado atual da exibição de alvos.
 * @param onAutoRefreshToggle Ação ao alternar o auto-refresh.
 * @param isAutoRefreshOn Estado atual do auto-refresh.
 * @param canRefresh Controla se o botão Refresh está ativo.
 * @param modifier Modificador para estilização adicional.
 * @return Composable da barra de menu.
 */
@Composable
fun MenuBar(
    // Ações do Menu Game
    onNewGame: () -> Unit,
    onJoinGame: () -> Unit = {}, // Default vazio por enquanto
    onRefresh: () -> Unit,
    onExit: () -> Unit,

    // Ações do Menu Play
    onPass: () -> Unit,
    canPass: Boolean, // Controla se o botão Pass está ativo

    // Ações do Menu Options
    onShowTargetsToggle: () -> Unit,
    isShowTargetsOn: Boolean,
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
            // Menus da Barra: Game, Play, Options
            MenuDropdown(label = "Game") { closeMenu ->
                DropdownMenuItem(
                    text = { Text("New") },
                    onClick = { closeMenu(); onNewGame() }
                )
                DropdownMenuItem(
                    text = { Text("Join") },
                    onClick = { closeMenu(); onJoinGame() }
                )
                DropdownMenuItem(
                    text = { Text("Refresh") },
                    onClick = { closeMenu(); onRefresh() },
                    enabled = canRefresh // [Requisito]: Só ativo se autorefresh OFF e não for minha vez
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Exit") },
                    onClick = { closeMenu(); onExit() }
                )
            }

            MenuDropdown(label = "Play") { closeMenu ->
                DropdownMenuItem(
                    text = { Text("Pass") },
                    onClick = { closeMenu(); onPass() },
                    enabled = canPass // [Requisito]: Só ativo se não houver jogadas possíveis
                )
            }

            MenuDropdown(label = "Options") { closeMenu ->
                DropdownMenuItem(
                    text = {
                        val check = if (isShowTargetsOn) "✔ " else ""
                        Text("${check}Show Targets")
                    },
                    onClick = { closeMenu(); onShowTargetsToggle() }
                )
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
 * @param label Texto do botão do menu.
 * @param content Conteúdo do menu dropdown.
 * @return Composable do menu dropdown.
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
            // Passamos a função de fechar para dentro dos itens
            content { isOpen.value = false }
        }
    }
}