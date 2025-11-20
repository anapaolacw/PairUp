package com.example.pairup.data

data class Round(
    val roundNumber: Int,
    val matches: MutableList<Match> = mutableListOf(),
    val playersResting: MutableList<Player> = mutableListOf()
) {
    fun isCompleted(): Boolean = matches.all { it.isCompleted }

    fun getTotalMatches(): Int = matches.size

    fun getCompletedMatches(): Int = matches.count { it.isCompleted }
}

