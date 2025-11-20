package com.example.pairup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.pairup.data.Match
import com.example.pairup.databinding.DialogScoreInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ScoreInputDialog(
    private val match: Match,
    private val onScoreSaved: (Int, Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogScoreInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogScoreInputBinding.inflate(LayoutInflater.from(context))

        setupUI()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupUI() {
        val team1Text = "${match.team1Player1.getDisplayName()} & ${match.team1Player2.getDisplayName()}"
        val team2Text = "${match.team2Player1.getDisplayName()} & ${match.team2Player2.getDisplayName()}"

        binding.tvTeam1Names.text = team1Text
        binding.tvTeam2Names.text = team2Text

        // Pre-fill if match already has scores
        if (match.isCompleted) {
            binding.etTeam1Score.setText(match.team1Score.toString())
            binding.etTeam2Score.setText(match.team2Score.toString())
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveScore()
        }
    }

    private fun saveScore() {
        val team1ScoreText = binding.etTeam1Score.text.toString()
        val team2ScoreText = binding.etTeam2Score.text.toString()

        val team1Score = team1ScoreText.toIntOrNull()
        val team2Score = team2ScoreText.toIntOrNull()

        if (team1Score == null || team2Score == null) {
            Toast.makeText(context, "Please enter valid scores", Toast.LENGTH_SHORT).show()
            return
        }

        onScoreSaved(team1Score, team2Score)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

