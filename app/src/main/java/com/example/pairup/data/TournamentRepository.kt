package com.example.pairup.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TournamentRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "tournament_prefs"
        private const val KEY_TOURNAMENT = "current_tournament"
        private const val KEY_HAS_ACTIVE = "has_active_tournament"
    }

    /**
     * Saves the current tournament state
     */
    fun saveTournament(tournament: Tournament) {
        val json = gson.toJson(tournament)
        sharedPreferences.edit()
            .putString(KEY_TOURNAMENT, json)
            .putBoolean(KEY_HAS_ACTIVE, tournament.isActive)
            .apply()
    }

    /**
     * Loads the saved tournament, or null if none exists
     */
    fun loadTournament(): Tournament? {
        val json = sharedPreferences.getString(KEY_TOURNAMENT, null) ?: return null
        return try {
            gson.fromJson(json, Tournament::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks if there's an active tournament
     */
    fun hasActiveTournament(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_ACTIVE, false)
    }

    /**
     * Clears the saved tournament
     */
    fun clearTournament() {
        sharedPreferences.edit()
            .remove(KEY_TOURNAMENT)
            .putBoolean(KEY_HAS_ACTIVE, false)
            .apply()
    }
}

