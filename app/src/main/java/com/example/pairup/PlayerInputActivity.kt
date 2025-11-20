package com.example.pairup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pairup.adapter.PlayerInputAdapter
import com.example.pairup.data.Player
import com.example.pairup.databinding.ActivityPlayerInputBinding
import com.example.pairup.viewmodel.TournamentViewModel

class PlayerInputActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INPUT_MODE = "input_mode"
        const val MODE_WITH_NAMES = "with_names"
        const val MODE_ANONYMOUS = "anonymous"
    }

    private lateinit var binding: ActivityPlayerInputBinding
    private val viewModel: TournamentViewModel by viewModels()
    private lateinit var inputMode: String
    private val players = mutableListOf<Player>()
    private lateinit var adapter: PlayerInputAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMode = intent.getStringExtra(EXTRA_INPUT_MODE) ?: MODE_WITH_NAMES

        setupUI()
    }

    private fun setupUI() {
        // Setup RecyclerView
        adapter = PlayerInputAdapter(players) { player ->
            players.remove(player)
            adapter.notifyDataSetChanged()
            updatePlayerCount()
        }
        binding.rvPlayers.layoutManager = LinearLayoutManager(this)
        binding.rvPlayers.adapter = adapter

        when (inputMode) {
            MODE_WITH_NAMES -> setupNamedMode()
            MODE_ANONYMOUS -> setupAnonymousMode()
        }

        binding.btnStartTournament.setOnClickListener {
            startTournament()
        }
    }

    private fun setupNamedMode() {
        binding.tilPlayerName.visibility = View.VISIBLE
        binding.btnAddPlayer.visibility = View.VISIBLE
        binding.tvPlayersAdded.visibility = View.VISIBLE
        binding.rvPlayers.visibility = View.VISIBLE

        binding.btnAddPlayer.setOnClickListener {
            val name = binding.etPlayerName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val player = Player(name = name, isAnonymous = false)
            players.add(player)
            adapter.notifyItemInserted(players.size - 1)
            binding.etPlayerName.text?.clear()
            updatePlayerCount()
        }

        updatePlayerCount()
    }

    private fun setupAnonymousMode() {
        binding.tilPlayerCount.visibility = View.VISIBLE
    }

    private fun updatePlayerCount() {
        binding.tvPlayersAdded.text = getString(R.string.players_added, players.size)
    }

    private fun startTournament() {
        when (inputMode) {
            MODE_WITH_NAMES -> {
                if (players.size < 4) {
                    Toast.makeText(this, R.string.error_min_players, Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.initializeTournamentWithNames(players.map { it.name })
            }
            MODE_ANONYMOUS -> {
                val countText = binding.etPlayerCount.text.toString()
                val count = countText.toIntOrNull()
                if (count == null || count < 4) {
                    Toast.makeText(this, R.string.error_invalid_count, Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.initializeTournamentWithCount(count)
            }
        }

        // Start first round
        viewModel.startNewRound()

        // Navigate to round activity
        val intent = Intent(this, RoundActivity::class.java)
        startActivity(intent)
        finish()
    }
}

