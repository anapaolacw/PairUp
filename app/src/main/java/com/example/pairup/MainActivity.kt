package com.example.pairup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pairup.databinding.ActivityMainBinding
import com.example.pairup.viewmodel.TournamentViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForActiveTournament()
        setupUI()
    }

    private fun checkForActiveTournament() {
        if (viewModel.hasActiveTournament()) {
            showResumeTournamentDialog()
        }
    }

    private fun showResumeTournamentDialog() {
        AlertDialog.Builder(this)
            .setTitle("Resume Tournament")
            .setMessage("You have an active tournament. Would you like to resume it?")
            .setPositiveButton("Resume") { _, _ ->
                resumeTournament()
            }
            .setNegativeButton("Start New") { _, _ ->
                viewModel.clearTournament()
            }
            .setCancelable(false)
            .show()
    }

    private fun resumeTournament() {
        val intent = Intent(this, RoundActivity::class.java)
        startActivity(intent)
    }

    private fun setupUI() {
        binding.btnWithNames.setOnClickListener {
            val intent = Intent(this, PlayerInputActivity::class.java)
            intent.putExtra(PlayerInputActivity.EXTRA_INPUT_MODE, PlayerInputActivity.MODE_WITH_NAMES)
            startActivity(intent)
        }

        binding.btnAnonymous.setOnClickListener {
            val intent = Intent(this, PlayerInputActivity::class.java)
            intent.putExtra(PlayerInputActivity.EXTRA_INPUT_MODE, PlayerInputActivity.MODE_ANONYMOUS)
            startActivity(intent)
        }
    }
}

