package reversi_ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import reversi.core.Reversi
import reversi.core.ReversiState
import reversi.model.ReversiColor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private const val STATES_DIR = "game_states"

/** Wrapper for serializing both state and first player. */
data class SavedGame(
    val state: ReversiState,
    val firstPlayer: ReversiColor
) : Serializable

object GameFileManager {

    suspend fun createNewGameFile(name: String, game: Reversi, firstPlayer: ReversiColor): Boolean =
        withContext(Dispatchers.IO) {
            val dir = File(STATES_DIR)
            if (!dir.exists()) dir.mkdir()

            val file = File(dir, "$name.dat")
            if (file.exists()) return@withContext false

            val savedGame = SavedGame(game.currentState, firstPlayer)
            ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(savedGame) }
            return@withContext true
        }

    suspend fun saveGameState(name: String, game: Reversi) =
        withContext(Dispatchers.IO) {
            val dir = File(STATES_DIR)
            if (!dir.exists()) dir.mkdir()

            val file = File(dir, "$name.dat")
            val existing = loadSavedGame(name) ?: error("No saved game to overwrite, use createNewGameFile first")
            val newSaved = existing.copy(state = game.currentState)
            ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(newSaved) }
        }

    suspend fun loadSavedGame(name: String): SavedGame? = withContext(Dispatchers.IO) {
        val file = File(STATES_DIR, "$name.dat")
        if (!file.exists()) return@withContext null

        ObjectInputStream(FileInputStream(file)).use { stream ->
            val saved = stream.readObject() as SavedGame
            // Recreate state to refresh lazy properties (as in original)
            val refreshedState = saved.state.copy()
            return@withContext saved.copy(state = refreshedState)
        }
    }
}
