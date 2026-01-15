package reversi_ui

import io.mockk.*
import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.mongodb.client.FindIterable
import com.mongodb.client.result.UpdateResult
import org.bson.conversions.Bson // Importante para o Mockk reconhecer o tipo
import reversi_data.mongodb.GameState
import reversi_data.mongodb.MongoGameManager
import reversi_data.mongodb.MongoRepository

class ManagerSecurityTest {

    @BeforeEach
    fun setup() {
        // Mock do Repositório para não ir à BD real
        mockkObject(MongoRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // Helper para simular uma resposta da BD
    private fun mockFindGame(gameName: String, state: GameState?) {
        // Criamos o mock do Iterable (o resultado do .find())
        val mockIterable = mockk<FindIterable<GameState>>()

        // CORREÇÃO 1: Usamos any<Bson>() para resolver o erro "Cannot infer type T"
        // Isto diz: "Se chamarem o find com QUALQUER filtro Bson, devolve o nosso mockIterable"
        every {
            MongoRepository.gamesCollection.find(any<Bson>())
        } returns mockIterable

        // Configuramos o comportamento do .first()
        every { mockIterable.first() } returns state

        // CORREÇÃO 2: Removemos 'mockCursor' pois não estava a ser usado (Unused variable)
    }

    @Test
    fun `creator can always enter the game`() = runTest {
        val gameName = "MyGame"
        val myId = "User_Creator_123"

        // Jogo onde eu sou o Player1
        val gameState = GameState().apply {
            this.gameName = gameName
            this.player1Id = myId
            this.player2Id = "Other_Guy"
        }

        mockFindGame(gameName, gameState)

        // Act
        val canEnter = MongoGameManager.joinGameAsPlayer2(gameName, myId)

        // Assert
        assertTrue(canEnter, "O criador deve conseguir sempre entrar (Re-join)")
    }

    @Test
    fun `player2 can always re-enter the game`() = runTest {
        val gameName = "MyGame"
        val myId = "User_Player2_456"

        // Jogo onde eu já sou o Player2
        val gameState = GameState().apply {
            this.gameName = gameName
            this.player1Id = "Creator_Guy"
            this.player2Id = myId
        }

        mockFindGame(gameName, gameState)

        // Act
        val canEnter = MongoGameManager.joinGameAsPlayer2(gameName, myId)

        // Assert
        assertTrue(canEnter, "O Player 2 registado deve conseguir re-entrar")
    }

    @Test
    fun `new player can take empty slot`() = runTest {
        val gameName = "OpenGame"
        val myId = "New_User_789"

        // Jogo com slot 2 vazio
        val gameState = GameState().apply {
            this.gameName = gameName
            this.player1Id = "Creator_Guy"
            this.player2Id = null // Vazio
        }

        mockFindGame(gameName, gameState)

        // Simular que o update na BD funciona
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.modifiedCount } returns 1L // Retorna Long

        every {
            MongoRepository.gamesCollection.updateOne(any<Bson>(), any<Bson>())
        } returns mockUpdateResult

        // Act
        val canEnter = MongoGameManager.joinGameAsPlayer2(gameName, myId)

        // Assert
        assertTrue(canEnter, "Um novo jogador deve conseguir ocupar um lugar vazio")
    }

    @Test
    fun `stranger cannot enter full game`() = runTest {
        val gameName = "FullGame"
        val myId = "Stranger_000"

        // Jogo cheio com outras pessoas
        val gameState = GameState().apply {
            this.gameName = gameName
            this.player1Id = "Creator_Guy"
            this.player2Id = "Opponent_Guy"
        }

        mockFindGame(gameName, gameState)

        // Act
        val canEnter = MongoGameManager.joinGameAsPlayer2(gameName, myId)

        // Assert
        assertFalse(canEnter, "Um estranho NÃO deve conseguir entrar num jogo cheio")
    }

    @Test
    fun `cannot enter non-existent game`() = runTest {
        val gameName = "GhostGame"
        mockFindGame(gameName, null) // Jogo não existe (retorna null)

        val canEnter = MongoGameManager.joinGameAsPlayer2(gameName, "AnyUser")

        assertFalse(canEnter, "Não se pode entrar num jogo que não existe")
    }
}