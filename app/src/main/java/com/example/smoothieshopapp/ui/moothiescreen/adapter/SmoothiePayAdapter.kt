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
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.priceFormatted
import com.example.smoothieshopapp.util.loadImageWithImageUrl

class SmoothiePayAdapter(
    private val onClickBtnIncrease: (Cart) -> Unit,
    private val onClickBtnReduce: (Cart) -> Unit,
    private val onClickBtnRemove: (Cart) -> Unit
) : ListAdapter<Cart, SmoothiePayAdapter.SmoothiePayViewHolder>(DiffCallback) {
    class SmoothiePayViewHolder(private val binding: SmoothiePayItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binding data for SmoothiePayItem
         */
        fun bind(
            cart: Cart,
            onClickBtnIncrease: (Cart) -> Unit,
            onClickBtnReduce: (Cart) -> Unit,
            onClickBtnRemove: (Cart) -> Unit
        ) {
            binding.name.text = cart.smoothie.name
            binding.price.text = cart.smoothie.priceFormatted()
            binding.image.loadImageWithImageUrl(cart.smoothie.imageUrl)

            // Set quantity
            binding.quantity.text = cart.quantity.toString()

            // Set event onclick for btnIncrease and btnReduce
            binding.btnIncrease.setOnClickListener {
                onClickBtnIncrease(cart)
            }
            binding.btnReduce.setOnClickListener {
                onClickBtnReduce(cart)
            }
            // Set event onclick for btnRemove
            binding.btnRemove.setOnClickListener {
                onClickBtnRemove(cart)
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
            onClickBtnIncrease,
            onClickBtnReduce,
            onClickBtnRemove
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.smoothie.id == newItem.smoothie.id
            }

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.quantity == newItem.quantity
            }
        }
    }
}
