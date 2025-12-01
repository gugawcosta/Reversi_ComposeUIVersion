package org.example.reversi.ui

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
            Screen.START -> StartScreen(
                onEnterGame = { currentScreen = Screen.GAME },
                onCreateGame = { currentScreen = Screen.CREATE },
                onResolution = { currentScreen = Screen.RESOLUTION }
            )

            Screen.CREATE -> {
                // desenha a StartScreen por baixo (mantendo o background/visual igual ao Join dialog)
                StartScreen(
                    onEnterGame = { currentScreen = Screen.GAME },
                    onCreateGame = { currentScreen = Screen.CREATE },
                    onResolution = { currentScreen = Screen.RESOLUTION }
                )

                // sobrepõe o diálogo de criar jogo
                CreateGameScreen(
                    onConfirm = { _, _ ->
                        // navegar para jogo (poderia passar parâmetros ao ViewModel conforme necessário)
                        currentScreen = Screen.GAME
                    },
                    onDismiss = { currentScreen = Screen.START }
                )
            }

            Screen.GAME -> GameApp()

            Screen.RESOLUTION -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F7A0F))
                ) {
                    ResolutionScreen(
                        onBack = { currentScreen = Screen.START },
                        onSetWindowSize = { wDp: Dp, hDp: Dp ->
                            windowState.size = DpSize(wDp, hDp)
                            windowState.position = WindowPosition.Aligned(Alignment.Center)
                        },
                        onToggleFullscreen = { fullscreen ->
                            val frame = Frame.getFrames().firstOrNull { it.title == "Reversi" }
                            frame?.let {
                                if (fullscreen) {
                                    it.extendedState = Frame.MAXIMIZED_BOTH
                                } else {
                                    it.extendedState = Frame.NORMAL
                                }
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
