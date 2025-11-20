package com.example.pairup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pairup.adapter.MatchAdapter
import com.example.pairup.databinding.ActivityRoundBinding
import com.example.pairup.viewmodel.TournamentViewModel

class RoundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoundBinding
    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupUI()
    }

    private fun setupObservers() {
        viewModel.currentRound.observe(this) { round ->
            round?.let {
                binding.tvRoundTitle.text = getString(R.string.round_title, round.roundNumber)

                val adapter = MatchAdapter(round.matches) { match ->
                    showScoreDialog(match)
                }
                binding.rvMatches.layoutManager = LinearLayoutManager(this)
                binding.rvMatches.adapter = adapter

                // Update button state
                updateButtonState()
            }
        }
    }

    private fun setupUI() {
        binding.btnNextRound.setOnClickListener {
            if (viewModel.isCurrentRoundCompleted()) {
                viewModel.startNewRound()
            } else {
                Toast.makeText(
                    this,
                    "Please complete all matches before starting next round",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnEndTournament.setOnClickListener {
            viewModel.endTournament()
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showScoreDialog(match: com.example.pairup.data.Match) {
        val dialog = ScoreInputDialog(match) { team1Score, team2Score ->
            viewModel.updateMatchScore(match, team1Score, team2Score)
            // Refresh the adapter
            viewModel.currentRound.value?.let { round ->
                val adapter = MatchAdapter(round.matches) { m ->
                    showScoreDialog(m)
                }
                binding.rvMatches.adapter = adapter
            }
            updateButtonState()
        }
        dialog.show(supportFragmentManager, "ScoreInputDialog")
    }

    private fun updateButtonState() {
        binding.btnNextRound.isEnabled = viewModel.isCurrentRoundCompleted()
    }
}

