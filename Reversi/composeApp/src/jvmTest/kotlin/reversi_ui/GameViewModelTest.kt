package reversi_ui

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reversi.core.Reversi
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi_data.mongodb.MongoGameManager
import reversi_viewmodel.GameViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        // Define o dispatcher principal para testes (para substituir o UI Thread)
        Dispatchers.setMain(testDispatcher)

        // Mocka o Singleton do Mongo para não bater na BD real
        mockkObject(MongoGameManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // Limpa os mocks
    }

    @Test
    fun `Singleplayer - click valid cell updates state without network`() = runTest {
        // Arrange
        val game = Reversi(ReversiBoard(8, 8))
        val viewModel = GameViewModel(game, gameName = "") // Nome vazio = Singleplayer

        val validMove = Cell(3, 4) // Uma jogada clássica válida inicial para as Pretas

        // Act
        viewModel.onCellClick(validMove)

        // Assert
        // 1. O estado mudou?
        assertNotEquals(ReversiColor.BLACK, viewModel.currentPlayer, "O turno devia ter mudado para Brancas")
        assertNotNull(viewModel.getPieceAt(validMove), "Devia haver uma peça na célula clicada")

        // 2. Garante que NÃO chamou o Mongo
        coVerify(exactly = 0) { MongoGameManager.updateGameState(any(), any()) }
    }

    @Test
    fun `Multiplayer - click valid cell sends data to cloud`() = runTest {
        // Arrange
        val gameName = "TestRoom"
        val game = Reversi(ReversiBoard(8, 8))

        // Simula que o update no Mongo devolve Sucesso (true)
        coEvery { MongoGameManager.updateGameState(any(), any()) } returns true

        val viewModel = GameViewModel(
            game,
            gameName = gameName,
            localPlayerColor = ReversiColor.BLACK,
            dispatcher = testDispatcher
        )

        val validMove = Cell(3, 4) // Jogada válida inicial

        // Act
        viewModel.onCellClick(validMove)

        // Avança as corrotinas (agora funciona porque o VM usa o mesmo dispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        // 1. O estado local atualizou
        assertNotNull(viewModel.getPieceAt(validMove), "A peça devia ter sido colocada localmente")

        // 2. Garante que CHAMOU o Mongo
        coVerify(exactly = 1) { MongoGameManager.updateGameState(gameName, any()) }

        // 3. Verifica feedback visual
        assertTrue(
            viewModel.statusMessage.contains("Jogada enviada") || viewModel.statusMessage.contains("Vez das"),
            "Status esperado devia indicar sucesso. Atual: '${viewModel.statusMessage}'"
        )
    }

    @Test
    fun `Multiplayer - blocking move if not player turn`() = runTest {
        // Arrange
        val game = Reversi(ReversiBoard(8, 8))
        // Eu sou as BRANCAS, mas o jogo começa com PRETAS
        val viewModel = GameViewModel(game, gameName = "TestRoom", localPlayerColor = ReversiColor.WHITE)

        val validMoveForBlack = Cell(3, 4)

        // Act
        viewModel.onCellClick(validMoveForBlack)

        // Assert
        // O turno NÃO deve mudar, porque não era a minha vez
        assertEquals(ReversiColor.BLACK, viewModel.currentPlayer)
        // A mensagem deve avisar
        assertEquals("Não é a tua vez!", viewModel.statusMessage)

        // Mongo não deve ser chamado
        coVerify(exactly = 0) { MongoGameManager.updateGameState(any(), any()) }
    }
}