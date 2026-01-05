import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import reversi.composeapp.generated.resources.Res
import reversi.composeapp.generated.resources.reversi
import java.awt.Frame

@Suppress("AssignedValueIsNeverRead")
fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(800.dp, 600.dp),
        position = WindowPosition.Aligned(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Reversi",
        icon = painterResource(Res.drawable.reversi),
        state = windowState,
        resizable = false
    ) {
        var currentScreen by remember { mutableStateOf(Screen.START) }

        when (currentScreen) {
            Screen.START -> _root_ide_package_.reversi_ui.StartScreen(
                onEnterGame = { currentScreen = Screen.GAME },
                onCreateGame = { currentScreen = Screen.CREATE },
                onResolution = { currentScreen = Screen.RESOLUTION }
            )

            Screen.CREATE -> {
                // desenha a StartScreen por baixo (mantendo o background/visual igual ao Join dialog)
                _root_ide_package_.reversi_ui.StartScreen(
                    onEnterGame = { currentScreen = Screen.GAME },
                    onCreateGame = { currentScreen = Screen.CREATE },
                    onResolution = { currentScreen = Screen.RESOLUTION }
                )

                // sobrepõe o diálogo de criar jogo
                _root_ide_package_.reversi_ui.CreateGameScreen(
                    onConfirm = { _, _ ->
                        // navegar para jogo (poderia passar parâmetros ao ViewModel conforme necessário)
                        currentScreen = Screen.GAME
                    },
                    onDismiss = { currentScreen = Screen.START }
                )
            }

            Screen.GAME -> _root_ide_package_.reversi_ui.GameApp()

            Screen.RESOLUTION -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F7A0F))
                ) {
                    _root_ide_package_.reversi_ui.ResolutionScreen(
                        onBack = { currentScreen = Screen.START },
                        onSetWindowSize = { wDp: Dp, hDp: Dp ->
                            windowState.size = DpSize(wDp, hDp)
                            windowState.position = WindowPosition.Aligned(Alignment.Center)
                        },
                        onToggleFullscreen = { fullscreen ->
                            val frame = Frame.getFrames().firstOrNull { it.title == "Reversi" }
                            frame?.let {
                                if (fullscreen)
                                    it.extendedState = Frame.MAXIMIZED_BOTH
                                else
                                    it.extendedState = Frame.NORMAL
                            }
                        }
                    )
                }
            }
        }
    }
}

enum class Screen {
    START,
    CREATE,
    GAME,
    RESOLUTION
}

// O jogo ta bonito e bem! O front end code ta bom
// A parte de importar pode tar cook e algumas coisas q n consigo fazer (compreensivel)
// Cenas da logica do jogo (passar a jogada) (consecutive passes transition to end state)

// If current passes == Max consecutive passes -> transition state

// BASE DE DADOS (MongoDB) atualizacao automatica a partir de la + atualizacao manual

// Haver lobby... lobby ta conectado a base de dados
// No lobby tem jogos guardados, preview do tabuleiro, proximo jogador, acesso ao jogo?
// O q acontece no lobby?
// no need for lobby? (extra)