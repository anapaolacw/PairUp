package com.example.pairup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pairup.adapter.ResultsAdapter
import com.example.pairup.databinding.ActivityResultsBinding
import com.example.pairup.viewmodel.TournamentViewModel

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupUI()
    }

    private fun setupObservers() {
        viewModel.rankings.observe(this) { rankings ->
            val adapter = ResultsAdapter(rankings)
            binding.rvResults.layoutManager = LinearLayoutManager(this)
            binding.rvResults.adapter = adapter
        }
    }

    private fun setupUI() {
        binding.btnNewTournament.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

