package com.example.pairup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pairup.data.Match
import com.example.pairup.data.Player
import com.example.pairup.data.Round
import com.example.pairup.data.Tournament
import com.example.pairup.data.TournamentRepository
import com.example.pairup.logic.TournamentManager

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val tournamentManager = TournamentManager()
    private val repository = TournamentRepository(application)

    private val _tournament = MutableLiveData<Tournament>()
    val tournament: LiveData<Tournament> = _tournament

    private val _currentRound = MutableLiveData<Round?>()
    val currentRound: LiveData<Round?> = _currentRound

    private val _rankings = MutableLiveData<List<Player>>()
    val rankings: LiveData<List<Player>> = _rankings

    init {
        // Try to load saved tournament
        loadSavedTournament()
    }

    /**
     * Loads a saved tournament if one exists
     */
    private fun loadSavedTournament() {
        repository.loadTournament()?.let { savedTournament ->
            _tournament.value = savedTournament
            savedTournament.getCurrentRound()?.let { round ->
                _currentRound.value = round
            }
        }
    }

    /**
     * Checks if there's an active tournament
     */
    fun hasActiveTournament(): Boolean {
        return repository.hasActiveTournament()
    }

    /**
     * Clears the current tournament
     */
    fun clearTournament() {
        repository.clearTournament()
        _tournament.value = null
        _currentRound.value = null
        _rankings.value = null
    }

    /**
     * Saves the current tournament state
     */
    private fun saveTournament() {
        _tournament.value?.let { tournament ->
            repository.saveTournament(tournament)
        }
    }

    /**
     * Initialize a new tournament with anonymous players
     */
    fun initializeTournamentWithCount(playerCount: Int) {
        val players = tournamentManager.createAnonymousPlayers(playerCount)
        val newTournament = Tournament(players = players.toMutableList())
        _tournament.value = newTournament
        saveTournament()
    }

    /**
     * Initialize a new tournament with named players
     */
    fun initializeTournamentWithNames(playerNames: List<String>) {
        val players = tournamentManager.createNamedPlayers(playerNames)
        val newTournament = Tournament(players = players.toMutableList())
        _tournament.value = newTournament
        saveTournament()
    }

    /**
     * Start the first round or create a new round
     */
    fun startNewRound() {
        val currentTournament = _tournament.value ?: return

        val newRound = tournamentManager.createNewRound(currentTournament)
        currentTournament.rounds.add(newRound)
        currentTournament.currentRoundIndex = currentTournament.rounds.size - 1

        _tournament.value = currentTournament
        _currentRound.value = newRound
        saveTournament()
    }

    /**
     * Update match score
     */
    fun updateMatchScore(match: Match, team1Score: Int, team2Score: Int) {
        tournamentManager.updateMatchScore(match, team1Score, team2Score)

        // Trigger update
        _tournament.value = _tournament.value
        _currentRound.value = _currentRound.value
        saveTournament()
    }

    /**
     * End the tournament and get final rankings
     */
    fun endTournament() {
        val currentTournament = _tournament.value ?: return
        currentTournament.isActive = false

        val rankings = tournamentManager.getFinalRankings(currentTournament)
        _rankings.value = rankings
        _tournament.value = currentTournament
        saveTournament()
    }

    /**
     * Get current tournament
     */
    fun getTournament(): Tournament? = _tournament.value

    /**
     * Check if current round is completed
     */
    fun isCurrentRoundCompleted(): Boolean {
        return _currentRound.value?.isCompleted() ?: false
    }
}

