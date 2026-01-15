package reversi_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPlacement
import org.jetbrains.compose.resources.painterResource
import reversi.composeapp.generated.resources.Res
import reversi.composeapp.generated.resources.reversi
import reversi.core.Reversi
import reversi.model.ReversiColor
import reversi_ui.screens.game.GameApp
import reversi_ui.screens.lobby.LobbyScreen
import reversi_ui.screens.settings.ResolutionScreen
import reversi_ui.screens.home.StartScreen

/**
 * Ponto de entrada da aplicação Reversi com Compose Multiplataforma.
 * Configura a janela principal e gerencia a navegação entre telas.
 * As telas disponíveis são:
 * 1. StartScreen: Menu inicial com opções para iniciar jogo local, criar/juntar
 * jogo online, ou mudar resolução.
 * 2. LobbyScreen: Tela para listar e juntar a jogos online existentes (MongoDB).
 * 3. GameApp: Tela principal do jogo Reversi, onde o jogo é
 * jogado.
 * 4. ResolutionScreen: Tela para ajustar a resolução e fullscreen.
 * Cada tela é representada por um enum Screen.
 * As variáveis activeGame, activeGameName e localPlayerColor
 * são usadas para passar o estado do jogo entre as telas.
 * A janela é inicializada com tamanho 800x600 e centrada na tela.
 * A janela não é redimensionável para manter a integridade do layout.
 */

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1024.dp, 768.dp),
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

        // Variáveis para passar o jogo do Menu/Lobby para o Tabuleiro
        var activeGame by remember { mutableStateOf<Reversi?>(null) }
        var activeGameName by remember { mutableStateOf("") }
        var localPlayerColor by remember { mutableStateOf(ReversiColor.BLACK) }

        when (currentScreen) {
            // Menu Inicial
            Screen.START -> {
                StartScreen(
                    onGameStart = { game, name, color ->
                        activeGame = game
                        activeGameName = name
                        localPlayerColor = color
                        currentScreen = Screen.GAME
                    },
                    onResolution = {
                        currentScreen = Screen.RESOLUTION
                    },
                    onOpenLobby = {
                        currentScreen = Screen.LOBBY // Vai para o Lobby
                    }
                )
            }
            // Lobby Multiplayer
            Screen.LOBBY -> {
                LobbyScreen(
                    onJoinGame = { game, name, color ->
                        activeGame = game
                        activeGameName = name
                        localPlayerColor = color
                        currentScreen = Screen.GAME // Vai para o jogo com os dados carregados
                    },
                    onBack = {
                        currentScreen = Screen.START
                    }
                )
            }
            // Tela do Jogo
            Screen.GAME -> {
                if (activeGame != null) {
                    GameApp(
                        game = activeGame!!,
                        gameName = activeGameName,
                        playerColor = localPlayerColor,
                        onExit = {
                            activeGame = null
                            currentScreen = Screen.START
                        }
                    )
                } else {
                    // Segurança: se não houver jogo, volta ao início
                    currentScreen = Screen.START
                }
            }
            // Configuração de Resolução
            Screen.RESOLUTION -> {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F7A0F))) {
                    ResolutionScreen(
                        onBack = { currentScreen = Screen.START },
                        onSetWindowSize = { w, h ->
                            // Garante que sai de fullscreen ao mudar resolução
                            windowState.placement = WindowPlacement.Floating
                            windowState.size = DpSize(w, h)
                            windowState.position = WindowPosition.Aligned(Alignment.Center)
                        },
                        onToggleFullscreen = { goFullscreen ->
                            if (goFullscreen) {
                                windowState.placement = WindowPlacement.Fullscreen
                            } else {
                                windowState.placement = WindowPlacement.Floating
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Enumeração das telas disponíveis na aplicação.
 */
enum class Screen { START, GAME, RESOLUTION, LOBBY }