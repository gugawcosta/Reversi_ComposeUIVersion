package mongodb

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

/**
 * Data class representing the state of a Reversi game for MongoDB storage.
 * @param gameName The unique name of the game.
 * @param board The current state of the game board as a string.
 * @param turn The player whose turn it is ("B" for black, "W" for white).
 * @param p1Color The color assigned to player who created ("B" or "W").
 * @param timestamp The last updated timestamp of the game state.
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
    var timestamp: Long
) {
    // Construtor vazio
    constructor() : this("", "", "", "", 0L)
}