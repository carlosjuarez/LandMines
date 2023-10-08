package com.juvcarl.landmines

data class GameState(
    val gameStatus: GameStatus = GameStatus.NOTSTARTED,
    val boardPositions: Array<CharArray>? = null,
    val gameDifficulty: GameDifficulty = GameDifficulty.easy
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (gameStatus != other.gameStatus) return false
        if (!boardPositions.contentDeepEquals(other.boardPositions)) return false
        if (gameDifficulty != other.gameDifficulty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gameStatus.hashCode()
        result = 31 * result + boardPositions.contentDeepHashCode()
        result = 31 * result + gameDifficulty.hashCode()
        return result
    }
}

sealed interface GameDifficulty{
    val cols: Int
    val rows: Int

    object easy: GameDifficulty {
        override val cols: Int = 9
        override val rows: Int = 9
    }
    object medium: GameDifficulty{
        override val cols: Int = 9
        override val rows: Int = 9
    }
    object difficult: GameDifficulty{
        override val cols: Int = 9
        override val rows: Int = 9
    }
}

sealed interface GameStatus{
    object NOTSTARTED : GameStatus
    object STARTED : GameStatus
    object WON : GameStatus
    object LOSE : GameStatus
}