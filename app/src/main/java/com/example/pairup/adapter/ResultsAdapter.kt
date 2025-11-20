package com.example.pairup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pairup.data.Player
import com.example.pairup.databinding.ItemPlayerResultBinding

class ResultsAdapter(
    private val players: List<Player>
) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(private val binding: ItemPlayerResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player, position: Int) {
            binding.tvRank.text = "#${position + 1}"
            binding.tvPlayerName.text = player.getDisplayName()
            binding.tvPlayerStats.text =
                "Wins: ${player.gamesWon} | Games: ${player.gamesPlayed} | Score: ${player.totalScore}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemPlayerResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(players[position], position)
    }

    override fun getItemCount(): Int = players.size
}

