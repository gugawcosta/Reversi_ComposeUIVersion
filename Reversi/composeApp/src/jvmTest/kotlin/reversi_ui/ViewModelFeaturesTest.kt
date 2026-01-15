package reversi.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reversi.core.Reversi
import reversi.framework.Cell
import reversi.model.ReversiBoard
import reversi.model.ReversiColor
import reversi_viewmodel.GameViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelFeaturesTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `singleplayer game initializes with correct flags`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "", dispatcher = testDispatcher)

        assertFalse(vm.isMultiplayer)
        assertFalse(vm.autoRefreshEnabled)
        assertEquals("Modo Singleplayer", vm.statusMessage)
        assertNull(vm.errorMessage)
    }

    @Test
    fun `multiplayer game initializes with correct flags`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "Sala1", dispatcher = testDispatcher)

        assertTrue(vm.isMultiplayer)
        assertTrue(vm.autoRefreshEnabled)
        assertEquals("A carregar jogo...", vm.statusMessage)
    }

    @Test
    fun `toggle targets flips state correctly`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "", dispatcher = testDispatcher)
        assertFalse(vm.showTargets) // Off by default

        vm.toggleTargets()
        assertTrue(vm.showTargets)

        vm.toggleTargets()
        assertFalse(vm.showTargets)
    }

    @Test
    fun `toggle auto refresh works in multiplayer`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "Sala1", dispatcher = testDispatcher)
        assertTrue(vm.autoRefreshEnabled) // On by default

        vm.toggleAutoRefresh()
        assertFalse(vm.autoRefreshEnabled)

        vm.toggleAutoRefresh()
        assertTrue(vm.autoRefreshEnabled)
    }

    @Test
    fun `toggle auto refresh does nothing in singleplayer`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "", dispatcher = testDispatcher)
        assertFalse(vm.autoRefreshEnabled)

        vm.toggleAutoRefresh()
        // Deve continuar false porque a função verifica 'if (isMultiplayer)'
        assertFalse(vm.autoRefreshEnabled, "Não deve ligar refresh em singleplayer")
    }

    @Test
    fun `viewModel correctly exposes current player from game state`() {
        val game = Reversi(ReversiBoard(8,8))
        val vm = GameViewModel(game, "", dispatcher = testDispatcher)

        assertEquals(ReversiColor.BLACK, vm.currentPlayer)
    }

    @Test
    fun `viewModel correctly exposes scores`() {
        val game = Reversi(ReversiBoard(8,8))
        val vm = GameViewModel(game, "", dispatcher = testDispatcher)

        assertEquals(2, vm.blackCount)
        assertEquals(2, vm.whiteCount)
    }

    @Test
    fun `getPieceAt returns correct piece`() {
        val game = Reversi(ReversiBoard(8,8))
        val vm = GameViewModel(game, "", dispatcher = testDispatcher)

        val piece = vm.getPieceAt(Cell(4, 4))
        assertNotNull(piece)
        assertEquals(ReversiColor.WHITE, piece?.color)
    }

    @Test
    fun `getPieceAt returns null for empty cell`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8,8)), "", dispatcher = testDispatcher)
        assertNull(vm.getPieceAt(Cell(1, 1)))
    }

    @Test
    fun `gameOver property reflects game state`() {
        val vm = GameViewModel(Reversi(ReversiBoard(8, 8)), "", dispatcher = testDispatcher)
        assertFalse(vm.gameOver)
    }
}