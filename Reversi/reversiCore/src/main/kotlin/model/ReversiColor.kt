package reversi.model

import reversi.framework.Team

/**
 * Represents the color (team) of a Reversi player.
 *
 * Implements the [Team] interface, allowing a Reversi player to be
 * identified by their color (BLACK or WHITE). This enum also provides
 * utility methods for Reversi-specific operations.
 */
enum class ReversiColor() : Team<ReversiColor> {

    /** The black player/team. */
    BLACK,

    /** The white player/team. */
    WHITE;

    /**
     * Returns the unique identifier of this team, which is the color itself.
     */
    override val id get() = this

    /**
     * Returns the opposite color of the current one.
     *
     * @return [ReversiColor.WHITE] if this is [BLACK], otherwise [ReversiColor.BLACK].
     */
    fun invertColor(): ReversiColor {
        return if (this == BLACK) WHITE else BLACK
    }
}
