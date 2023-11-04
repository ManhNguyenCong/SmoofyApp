package com.example.smoothieshopapp.ui.moothiescreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smoothieshopapp.databinding.SpecialityItemListBinding
import com.example.smoothieshopapp.model.Smoothie
import com.example.smoothieshopapp.util.loadImageWithImageUrl

class SpecialitiesSmoothieAdapter(
    private val showDetailSmoothie: (String) -> Unit
) : ListAdapter<Smoothie, SpecialitiesSmoothieAdapter.SpecialitiesSmoothieViewHolder>(DiffCallback) {
    class SpecialitiesSmoothieViewHolder(private val binding: SpecialityItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This function is used to bind data for a item
         */
        fun bind(smoothie: Smoothie) {
            if(smoothie.name.length > 12) {
                binding.name.text = smoothie.name.substring(0, 9) + "..."
            } else {
                binding.name.text = smoothie.name
            }

            // Load image
            binding.image.loadImageWithImageUrl(smoothie.imageUrl)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpecialitiesSmoothieViewHolder {
        return SpecialitiesSmoothieViewHolder(
            SpecialityItemListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialitiesSmoothieViewHolder, position: Int) {
        // Get current smoothie at position
        val current = getItem(position)
        // Set onclick for it
        holder.itemView.setOnClickListener {
            showDetailSmoothie(current.id)
        }
        // Bind data for it
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Smoothie>() {
            override fun areItemsTheSame(oldItem: Smoothie, newItem: Smoothie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Smoothie, newItem: Smoothie): Boolean {
                return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl
            }
        }
    }
}