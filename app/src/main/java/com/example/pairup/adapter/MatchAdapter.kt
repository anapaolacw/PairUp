package com.example.pairup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pairup.data.Match
import com.example.pairup.databinding.ItemMatchBinding

class MatchAdapter(
    private val matches: List<Match>,
    private val onEnterScore: (Match) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, position: Int) {
            binding.tvMatchTitle.text = "Match ${position + 1}"

            val team1Text = "${match.team1Player1.getDisplayName()} & ${match.team1Player2.getDisplayName()}"
            val team2Text = "${match.team2Player1.getDisplayName()} & ${match.team2Player2.getDisplayName()}"

            binding.tvTeam1.text = team1Text
            binding.tvTeam2.text = team2Text
            binding.tvTeam1Score.text = match.team1Score.toString()
            binding.tvTeam2Score.text = match.team2Score.toString()

            if (match.isCompleted) {
                binding.tvCompleted.visibility = View.VISIBLE
                binding.btnEnterScore.text = "Edit Score"
            } else {
                binding.tvCompleted.visibility = View.GONE
                binding.btnEnterScore.text = "Enter Score"
            }

            binding.btnEnterScore.setOnClickListener {
                onEnterScore(match)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position], position)
    }

    override fun getItemCount(): Int = matches.size
}

