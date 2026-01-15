package reversi_data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

/**
 * Data class representing the state of a Reversi game for MongoDB storage.
 * @param gameName The unique name of the game.
 * @param board The current state of the game board as a string.
 * @param turn The player whose turn it is ("B" for black, "W" for white).
 * @param p1Color The color assigned to player who created ("B" or "W").
 * @param timestamp The last updated timestamp of the game state.
 * @param boardSize The size of the board (e.g., 8 for 8x8).
 * @param player1Id The unique ID of the creator.
 * @param player2Id The unique ID of the opponent.
 */
data class GameState(
    @field:BsonId
    var gameName: String,

    @field:BsonProperty("board")
    var board: String,

    @field:BsonProperty("turn")
    var turn: String,

    @field:BsonProperty("p1Color")
    var p1Color: String,

    @field:BsonProperty("timestamp")
    var timestamp: Long,

    @field:BsonProperty("boardSize")
    var boardSize: Int = 8,

    @field:BsonProperty("player1Id")
    var player1Id: String = "",

    @field:BsonProperty("player2Id")
    var player2Id: String? = null
) {
    // Construtor vazio
    constructor() : this("", "", "", "", 0L)
}