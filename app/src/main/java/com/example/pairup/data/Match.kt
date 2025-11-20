package com.example.pairup.data

import java.util.UUID

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val team1Player1: Player,
    val team1Player2: Player,
    val team2Player1: Player,
    val team2Player2: Player,
    var team1Score: Int = 0,
    var team2Score: Int = 0,
    var isCompleted: Boolean = false
) {
    fun getTeam1Players(): List<Player> = listOf(team1Player1, team1Player2)
    fun getTeam2Players(): List<Player> = listOf(team2Player1, team2Player2)
    fun getAllPlayers(): List<Player> = getTeam1Players() + getTeam2Players()
}

