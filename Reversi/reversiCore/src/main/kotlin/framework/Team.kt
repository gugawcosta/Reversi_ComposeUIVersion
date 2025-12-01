package reversi.framework

/**
 * Represents a team or player in an adversarial board game.
 *
 * This interface provides a generic way to identify teams or players
 * using a type parameter [ID], allowing flexibility in the identifier
 * type (e.g., String, Int, or a custom class).
 *
 * @param ID the type used to uniquely identify the team or player.
 */
interface Team<ID> {

    /** The unique identifier of the team or player. */
    val id: ID
}
