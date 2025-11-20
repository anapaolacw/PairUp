package com.example.pairup.logic

import com.example.pairup.data.Match
import com.example.pairup.data.Player
import com.example.pairup.data.Round
import com.example.pairup.data.Tournament
import kotlin.random.Random

class TournamentManager {

    // Track partnership history: Map of "playerId1-playerId2" to count
    private val partnershipHistory = mutableMapOf<String, Int>()

    // Track opponent history: Map of "playerId1-playerId2" to count
    private val opponentHistory = mutableMapOf<String, Int>()

    /**
     * Creates a new round with optimized pairings
     * Uses advanced algorithm to maximize variety in partnerships and opponents
     * Ensures players who rested in the previous round don't rest again if possible
     */
    fun createNewRound(tournament: Tournament): Round {
        val roundNumber = tournament.rounds.size + 1
        val allPlayers = tournament.players.toMutableList()

        // Build history from previous rounds
        buildHistoryFromTournament(tournament)

        // Get players who rested in the previous round (if any)
        val previouslyRested = if (tournament.rounds.isNotEmpty()) {
            tournament.rounds.last().playersResting.toSet()
        } else {
            emptySet()
        }

        // Determine how many complete groups of 4 we can make
        val numCompleteGroups = allPlayers.size / 4
        val numPlayingPlayers = numCompleteGroups * 4

        // Select playing players (prioritize those who rested)
        val playingPlayers = selectPlayingPlayers(
            allPlayers,
            numPlayingPlayers,
            previouslyRested
        )
        val restingPlayers = allPlayers - playingPlayers.toSet()

        val round = Round(roundNumber)

        // Create optimized matches using the advanced algorithm
        val matches = createOptimizedMatches(playingPlayers, numCompleteGroups)
        round.matches.addAll(matches)

        // Add resting players
        restingPlayers.forEach { player ->
            player.restCount++
            round.playersResting.add(player)
        }

        return round
    }

    /**
     * Builds partnership and opponent history from all previous rounds
     */
    private fun buildHistoryFromTournament(tournament: Tournament) {
        partnershipHistory.clear()
        opponentHistory.clear()

        tournament.rounds.forEach { round ->
            round.matches.forEach { match ->
                // Record partnerships
                recordPartnership(match.team1Player1.id, match.team1Player2.id)
                recordPartnership(match.team2Player1.id, match.team2Player2.id)

                // Record opponents (all combinations between teams)
                listOf(match.team1Player1, match.team1Player2).forEach { p1 ->
                    listOf(match.team2Player1, match.team2Player2).forEach { p2 ->
                        recordOpponent(p1.id, p2.id)
                    }
                }
            }
        }
    }

    /**
     * Records a partnership between two players
     */
    private fun recordPartnership(id1: String, id2: String) {
        val key = createPairKey(id1, id2)
        partnershipHistory[key] = (partnershipHistory[key] ?: 0) + 1
    }

    /**
     * Records an opponent relationship between two players
     */
    private fun recordOpponent(id1: String, id2: String) {
        val key = createPairKey(id1, id2)
        opponentHistory[key] = (opponentHistory[key] ?: 0) + 1
    }

    /**
     * Creates a consistent key for a pair of player IDs
     */
    private fun createPairKey(id1: String, id2: String): String {
        return if (id1 < id2) "$id1-$id2" else "$id2-$id1"
    }

    /**
     * Gets the partnership count between two players
     */
    private fun getPartnershipCount(id1: String, id2: String): Int {
        return partnershipHistory[createPairKey(id1, id2)] ?: 0
    }

    /**
     * Gets the opponent count between two players
     */
    private fun getOpponentCount(id1: String, id2: String): Int {
        return opponentHistory[createPairKey(id1, id2)] ?: 0
    }

    /**
     * Selects which players will play this round, prioritizing those who rested
     */
    private fun selectPlayingPlayers(
        allPlayers: List<Player>,
        numNeeded: Int,
        previouslyRested: Set<Player>
    ): List<Player> {
        if (numNeeded >= allPlayers.size) {
            return allPlayers.shuffled()
        }

        val mustPlay = allPlayers.filter { it in previouslyRested }
        val canRest = allPlayers.filter { it !in previouslyRested }.shuffled()

        return if (mustPlay.size >= numNeeded) {
            mustPlay.shuffled().take(numNeeded)
        } else {
            mustPlay + canRest.take(numNeeded - mustPlay.size)
        }.shuffled()
    }

    /**
     * Creates optimized matches using greedy algorithm with backtracking
     * Tries to minimize repeated partnerships and opponents
     */
    private fun createOptimizedMatches(players: List<Player>, numMatches: Int): List<Match> {
        val matches = mutableListOf<Match>()
        val availablePlayers = players.toMutableList()

        repeat(numMatches) {
            val matchPlayers = selectBestMatchGroup(availablePlayers)
            if (matchPlayers.size == 4) {
                val match = createMatchFromGroup(matchPlayers)
                matches.add(match)
                matchPlayers.forEach { availablePlayers.remove(it) }
            }
        }

        return matches
    }

    /**
     * Selects the best group of 4 players for a match
     * Uses scoring to minimize repeated partnerships and opponents
     */
    private fun selectBestMatchGroup(availablePlayers: List<Player>): List<Player> {
        if (availablePlayers.size < 4) return emptyList()

        var bestGroup: List<Player>? = null
        var bestScore = Int.MAX_VALUE

        // Try multiple random combinations and pick the best
        val attempts = minOf(50, availablePlayers.size * 2)

        repeat(attempts) {
            val shuffled = availablePlayers.shuffled()
            val group = shuffled.take(4)
            val score = calculateGroupScore(group)

            if (score < bestScore) {
                bestScore = score
                bestGroup = group
            }

            // If we find a group with perfect score (all new combinations), use it
            if (score == 0) return@repeat
        }

        return bestGroup ?: availablePlayers.take(4)
    }

    /**
     * Calculates a score for a group of 4 players
     * Lower score = better (less repetition)
     */
    private fun calculateGroupScore(players: List<Player>): Int {
        if (players.size != 4) return Int.MAX_VALUE

        var score = 0

        // Try both possible team configurations and use the better one
        val config1Score = calculateConfigScore(
            players[0], players[1],  // Team 1
            players[2], players[3]   // Team 2
        )

        val config2Score = calculateConfigScore(
            players[0], players[2],  // Team 1
            players[1], players[3]   // Team 2
        )

        return minOf(config1Score, config2Score)
    }

    /**
     * Calculates score for a specific team configuration
     */
    private fun calculateConfigScore(p1: Player, p2: Player, p3: Player, p4: Player): Int {
        var score = 0

        // Partnership penalty (weight = 10)
        score += getPartnershipCount(p1.id, p2.id) * 10
        score += getPartnershipCount(p3.id, p4.id) * 10

        // Opponent penalty (weight = 3)
        score += getOpponentCount(p1.id, p3.id) * 3
        score += getOpponentCount(p1.id, p4.id) * 3
        score += getOpponentCount(p2.id, p3.id) * 3
        score += getOpponentCount(p2.id, p4.id) * 3

        return score
    }

    /**
     * Creates a match from a group of 4 players using the best team configuration
     */
    private fun createMatchFromGroup(players: List<Player>): Match {
        if (players.size != 4) throw IllegalArgumentException("Need exactly 4 players")

        // Determine best configuration
        val config1Score = calculateConfigScore(players[0], players[1], players[2], players[3])
        val config2Score = calculateConfigScore(players[0], players[2], players[1], players[3])

        return if (config1Score <= config2Score) {
            Match(
                team1Player1 = players[0],
                team1Player2 = players[1],
                team2Player1 = players[2],
                team2Player2 = players[3]
            )
        } else {
            Match(
                team1Player1 = players[0],
                team1Player2 = players[2],
                team2Player1 = players[1],
                team2Player2 = players[3]
            )
        }
    }

    /**
     * Updates player statistics after a match is completed
     */
    fun updateMatchScore(match: Match, team1Score: Int, team2Score: Int) {
        match.team1Score = team1Score
        match.team2Score = team2Score
        match.isCompleted = true

        // Determine winner and update player stats
        val team1Players = match.getTeam1Players()
        val team2Players = match.getTeam2Players()

        if (team1Score > team2Score) {
            // Team 1 wins
            team1Players.forEach { player ->
                player.gamesPlayed++
                player.gamesWon++
                player.totalScore += team1Score
            }
            team2Players.forEach { player ->
                player.gamesPlayed++
                player.totalScore += team2Score
            }
        } else if (team2Score > team1Score) {
            // Team 2 wins
            team2Players.forEach { player ->
                player.gamesPlayed++
                player.gamesWon++
                player.totalScore += team2Score
            }
            team1Players.forEach { player ->
                player.gamesPlayed++
                player.totalScore += team1Score
            }
        } else {
            // Tie - both teams played
            (team1Players + team2Players).forEach { player ->
                player.gamesPlayed++
                player.totalScore += team1Score // same as team2Score
            }
        }
    }

    /**
     * Gets the final rankings sorted by wins, then by total score
     */
    fun getFinalRankings(tournament: Tournament): List<Player> {
        return tournament.players.sortedWith(
            compareByDescending<Player> { it.gamesWon }
                .thenByDescending { it.totalScore }
                .thenBy { it.restCount }
        )
    }

    /**
     * Creates anonymous players based on count
     */
    fun createAnonymousPlayers(count: Int): List<Player> {
        return (1..count).map { i ->
            Player(name = i.toString(), isAnonymous = true)
        }
    }

    /**
     * Creates named players
     */
    fun createNamedPlayers(names: List<String>): List<Player> {
        return names.map { name ->
            Player(name = name.trim(), isAnonymous = false)
        }
    }
}
