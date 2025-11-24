package com.example.pairup.data

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isAnonymous: Boolean = false,
    var totalScore: Int = 0,
    var gamesPlayed: Int = 0,
    var gamesWon: Int = 0,
    var restCount: Int = 0
) {
    fun getDisplayName(): String {
        return if (isAnonymous) "Player $name" else name
    }
}


