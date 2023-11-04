package com.example.smoothieshopapp.ui.moothiescreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.SmoothiePayItemListBinding
import com.example.smoothieshopapp.model.Cart

class SmoothiePayAdapter(
    private val displaySmoothieById: (String, TextView, TextView, ImageView) -> Unit,
    private val onClickBtnIncrease: (String, String, Int) -> Unit,
    private val onClickBtnReduce: (String, String, Int) -> Unit,
    private val onClickBtnRemove: (String, String, Int) -> Unit
) : ListAdapter<Cart, SmoothiePayAdapter.SmoothiePayViewHolder>(DiffCallback) {
    class SmoothiePayViewHolder(private val binding: SmoothiePayItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binding data for SmoothiePayItem
         */
        fun bind(
            cart: Cart,
            displaySmoothieById: (String, TextView, TextView, ImageView) -> Unit,
            onClickBtnIncrease: (String, String, Int) -> Unit,
            onClickBtnReduce: (String, String, Int) -> Unit,
            onClickBtnRemove: (String, String, Int) -> Unit
        ) {
            // Set name and price
            displaySmoothieById(
                cart.smoothieId,
                binding.name,
                binding.price,
                binding.image
            )

            // Set quantity
            binding.quantity.text = cart.quantity.toString()

            // Set event onclick for btnIncrease and btnReduce
            binding.btnIncrease.setOnClickListener {
                onClickBtnIncrease(
                    cart.userId,
                    cart.smoothieId,
                    cart.quantity
                )
            }
            binding.btnReduce.setOnClickListener {
                onClickBtnReduce(
                    cart.userId,
                    cart.smoothieId,
                    cart.quantity
                )
            }
            // Set event onclick for btnRemove
            binding.btnRemove.setOnClickListener {
                onClickBtnRemove(
                    cart.userId,
                    cart.smoothieId,
                    cart.quantity
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmoothiePayViewHolder {
        return SmoothiePayViewHolder(
            SmoothiePayItemListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.smoothie_pay_item_list, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: SmoothiePayViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            displaySmoothieById,
            onClickBtnIncrease,
            onClickBtnReduce,
            onClickBtnRemove
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.userId == newItem.userId && oldItem.smoothieId == newItem.smoothieId
            }

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.quantity == newItem.quantity
            }
        }
    }
}
