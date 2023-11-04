package com.example.smoothieshopapp.ui.moothiescreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smoothieshopapp.databinding.SmoothieItemListBinding
import com.example.smoothieshopapp.model.Smoothie
import com.example.smoothieshopapp.model.priceFormatted
import com.example.smoothieshopapp.model.ratingFormatted
import com.example.smoothieshopapp.util.loadImageWithImageUrl

/**
 * This class is used to set adapter for smoothie recycler view
 *
 * @param showDetailSmoothie
 * @param setFavoriteSmoothie
 */
class SmoothieAdapter(
    private val showDetailSmoothie: (String) -> Unit,
    private val setFavoriteSmoothie: (String, ImageView) -> Unit
) : ListAdapter<Smoothie, SmoothieAdapter.SmoothieViewHolder>(DiffCallback) {
    class SmoothieViewHolder(private val binding: SmoothieItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This function is used to set events onclick and data for item
         *
         * @param smoothie
         * @param showDetailSmoothie
         * @param setFavoriteSmoothie
         */
        fun bind(
            smoothie: Smoothie,
            showDetailSmoothie: (String) -> Unit,
            setFavoriteSmoothie: (String, ImageView) -> Unit
        ) {
            // Set events onclick for item
            binding.image.setOnClickListener {
                showDetailSmoothie(smoothie.id)
            }
            binding.btnLike.setOnClickListener {
//                setFavoriteSmoothie(smoothie.id, smoothie.isFavorite)
            }

            // Bind data for item
            binding.name.text = smoothie.name
            binding.cost.text = smoothie.priceFormatted()
            binding.star.text = smoothie.ratingFormatted()

            // Load image
            binding.image.loadImageWithImageUrl(smoothie.imageUrl)

            // Set icon favorite for item
            setFavoriteSmoothie(smoothie.id, binding.btnLike)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmoothieViewHolder {
        return SmoothieViewHolder(
            SmoothieItemListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SmoothieViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            showDetailSmoothie,
            setFavoriteSmoothie
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Smoothie>() {
            override fun areItemsTheSame(oldItem: Smoothie, newItem: Smoothie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Smoothie, newItem: Smoothie): Boolean {
                return oldItem.name == newItem.name
                        && oldItem.rating == newItem.rating
                        && oldItem.price == newItem.price
                        && oldItem.imageUrl == newItem.imageUrl
                        && oldItem.quantityInStock == newItem.quantityInStock
            }
        }
    }
}