package com.example.pairup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pairup.data.Player
import com.example.pairup.databinding.ItemPlayerInputBinding

class PlayerInputAdapter(
    private val players: MutableList<Player>,
    private val onRemove: (Player) -> Unit
) : RecyclerView.Adapter<PlayerInputAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerInputBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.tvPlayerName.text = player.getDisplayName()
            binding.btnRemove.setOnClickListener {
                onRemove(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size
}

