package reversi.framework

/**
 * A generic interface representing an adversarial, turn-based board game.
 *
 * This interface defines the core structure and operations for any two-player
 * (or team-based) board game where players take turns performing actions
 * on a shared board. It is designed to be highly reusable across different
 * games such as Reversi, Chess, Checkers, or Tic-Tac-Toe.
 *
 * @param ID the type used to identify a team or player.
 * @param T the type representing the collection of pieces on the board.
 * @param S the type representing the game state, which must implement [State].
 * @param A the type representing a game action or move, which must implement [Action].
 */
interface AdversarialBoardGame<
        ID,
        T,
        S: AdversarialBoardGame.State<ID, T, A>,
        A: AdversarialBoardGame.Action
        > {

    /**
     * Represents a snapshot of the game state at a specific point in time.
     *
     * @param ID the type used to identify a team or player.
     * @param T the type representing the collection of pieces on the board.
     * @param A the type representing a game action or move.
     */
    interface State<ID, T, A> {

        /** The team whose turn it currently is. */
        val currentTurn: Team<ID>

        /** The current collection of pieces in the game. */
        val pieces: T

        /** The set of legal actions available to the current player. */
        val legalMoves: Set<A>

        /** Indicates whether the game has ended. */
        val isOver: Boolean

        /** The board associated with this state. */
        val board: Board

        val result: GameResult

        fun applyAction(action: A): State<ID, T, A>
    }

    /**
     * Represents a game action or move.
     *
     * Concrete implementations should define the specifics of an action,
     * such as the position to play or piece to move.
     */
    interface Action

    /** The current state of the game. */
    var currentState: S

    /** The board associated with this game. */
    val board: Board

    /**
     * Sets up a new game and returns the initial state.
     *
     * @return the initial game state after setup.
     */
    fun initialSetup(): S
}
