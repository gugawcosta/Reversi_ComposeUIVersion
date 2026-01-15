package reversi_ui

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class InputValidationTest {

    // --- Helpers para simular a lógica da UI ---
    private fun isValidGameName(name: String): Boolean = name.trim().isNotEmpty()

    // Função do Slider: boardSize = 4f + (index * 2)
    private fun calculateBoardSize(sliderIndex: Int): Int = 4 + (sliderIndex * 2)

    // Função Inversa (Valor -> Slider): (boardSize - 4) / 2
    private fun calculateSliderValue(boardSize: Int): Float = (boardSize - 4) / 2f

    @Test
    fun `simple name is valid`() {
        assertTrue(isValidGameName("Sala1"))
        assertTrue(isValidGameName("ReversiMasters"))
    }

    @Test
    fun `empty name is invalid`() {
        assertFalse(isValidGameName(""))
    }

    @Test
    fun `blank name is invalid`() {
        assertFalse(isValidGameName("   "))
        assertFalse(isValidGameName("\t\n"))
    }

    @Test
    fun `name with spaces around is valid`() {
        // A função isValidGameName faz trim() internamente
        assertTrue(isValidGameName("  Sala X  "))
    }

    @Test
    fun `name with special chars is valid`() {
        assertTrue(isValidGameName("Sala_do_Jogo!"))
        assertTrue(isValidGameName("12345"))
    }

    @Test
    fun `slider index 0 gives size 4`() {
        assertEquals(4, calculateBoardSize(0))
    }

    @Test
    fun `slider index 1 gives size 6`() {
        assertEquals(6, calculateBoardSize(1))
    }

    @Test
    fun `slider index 2 gives size 8`() {
        assertEquals(8, calculateBoardSize(2))
    }

    @Test
    fun `slider index 4 gives size 12`() {
        assertEquals(12, calculateBoardSize(4))
    }

    @Test
    fun `reverse calculation for UI display`() {
        // Se o tabuleiro for 8, o slider deve estar na posição 2.0
        assertEquals(2.0f, calculateSliderValue(8))

        // Se o tabuleiro for 4, o slider deve estar na posição 0.0
        assertEquals(0.0f, calculateSliderValue(4))

        // Se o tabuleiro for 12, o slider deve estar na posição 4.0
        assertEquals(4.0f, calculateSliderValue(12))
    }
}