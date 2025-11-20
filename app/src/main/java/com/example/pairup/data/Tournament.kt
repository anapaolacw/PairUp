package com.example.pairup.data

data class Tournament(
    val players: MutableList<Player> = mutableListOf(),
    val rounds: MutableList<Round> = mutableListOf(),
    var currentRoundIndex: Int = -1,
    var isActive: Boolean = true
) {
    fun getCurrentRound(): Round? {
        return if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size) {
            rounds[currentRoundIndex]
        } else null
    }

    fun getTotalRounds(): Int = rounds.size

    fun hasActiveRound(): Boolean = getCurrentRound() != null
}

