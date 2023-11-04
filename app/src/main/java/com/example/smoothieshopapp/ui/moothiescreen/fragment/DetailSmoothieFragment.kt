package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentDetailSmoothieBinding
import com.example.smoothieshopapp.model.Smoothie
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SpecialitiesSmoothieAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.example.smoothieshopapp.util.findNavControllerSafely
import com.example.smoothieshopapp.util.loadImageWithImageUrl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class DetailSmoothieFragment : Fragment() {

    // Arguments of DetailSmoothie in nav_graph
    private val args: DetailSmoothieFragmentArgs by navArgs()

    // Binding to fragment_detail_smoothie
    private lateinit var binding: FragmentDetailSmoothieBinding

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(SmoothieApi.dbRef)
    }

    // Current user
    private var user: FirebaseUser? = null

    // Smoothie which has id is args.id
    private var smoothie: Smoothie? = null

    // Recommend products adapter
    private var adapter: SpecialitiesSmoothieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailSmoothieBinding.inflate(inflater, container, false)

        //Set event add smoothie to cart
        binding.btnSubmit.setOnClickListener {
            addSmoothieToCart()
        }

        // Set open navigation view
        setNavigationView()
        // Load recycler view
        setRecyclerView()
        // Set up button adjust quantity
        setupQuantityAdjust()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get current user
        user = SmoothieApi.firebaseAuth.currentUser

        //Get smoothie by id
        viewModel.getSmoothieById(args.id).observe(this.viewLifecycleOwner) {
            smoothie = it
            binding()
        }
    }

    /**
     * This function is used to open/ close navigation view
     *
     */
    private fun setNavigationView() {
        binding.openNavView.setOnClickListener {
            val drawerLayout =
                activity?.findViewById<DrawerLayout>(R.id.drawerLayout) ?: return@setOnClickListener
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    /**
     * This function is used to set up button adjust quantity
     *
     */
    private fun setupQuantityAdjust() {
        // Setup button reduce quantity
        binding.btnReduce.setOnClickListener {
            // Get quantity
            var quantity = binding.quantity.text.toString().toInt()
            // Reduce quantity
            if (quantity > 0) {
                quantity--
                binding.quantity.text = quantity.toString()
            }
        }
        // Setup button reduce quantity
        binding.btnIncrease.setOnClickListener {
            smoothie?.also {
                // Get quantity
                var quantity = binding.quantity.text.toString().toInt()
                // Get quantity in stock
                val quantityInStock = it.quantityInStock
                // Increase quantity
                if (quantity < quantityInStock) {
                    quantity++
                    binding.quantity.text = quantity.toString()
                }
            }
        }
    }

    /**
     * This function is used to load recommend products recycler view
     */
    private fun setRecyclerView() {
        // Init adapter
        adapter = SpecialitiesSmoothieAdapter { id ->
            // Navigate to DetailSmoothieFragment with new smoothie choice
            val action = DetailSmoothieFragmentDirections.actionDetailSmoothieFragmentSelf(id)
            findNavControllerSafely()?.navigate(action)
        }
        // Set adapter for recycler view
        binding.recommendProductsRecyclerView.adapter = adapter
    }

    /**
     * This function is used to bind data for smoothie and
     * recommend products recycler view
     *
     */
    private fun binding() {
        //Todo set smoothie information
        user?.also { user ->
            smoothie?.also { smoothie ->
                // Set name smoothie
                binding.name.text = smoothie.name
                // Load image
                binding.image.loadImageWithImageUrl(smoothie.imageUrl)

                // Set favorite icon
                viewModel.getAllFavoriteByUserId(Firebase.auth.currentUser?.uid ?: "")
                    .observe(this.viewLifecycleOwner) { favorites ->
                        if (smoothie.id in favorites) {
                            // Set drawable favorite
                            binding.btnLike.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_round_favorite_24,
                                    null
                                )
                            )
                            // Set event remove favorite smoothie
                            binding.btnLike.setOnClickListener {
                                viewModel.removeFavorite(
                                    user.uid,
                                    smoothie.id
                                )
                            }
                        } else {
                            // Set drawable don't favorite
                            binding.btnLike.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_round_favorite_border_24,
                                    null
                                )
                            )
                            // Set event add favorite smoothie
                            binding.btnLike.setOnClickListener {
                                viewModel.addFavorite(
                                    user.uid,
                                    smoothie.id
                                )
                            }
                        }
                    }

                // Set rating
                binding.ratingBar.rating = smoothie.rating
                // Set price
                binding.price.text = String.format("%.2f$", smoothie.price)
            }
        }

        //SubmitList for recommend products recycler view
        viewModel.getAllSmoothies().observe(this.viewLifecycleOwner) { smoothies ->
            adapter?.also {
                it.submitList(
                    if (smoothies.size > 5) {
                        val indexRandom = (0..(smoothies.size - 1 - 5)).random()
                        smoothies.subList(indexRandom, indexRandom + 4)
                    } else {
                        smoothies
                    }
                )
            }
        }
    }

    /**
     * This function is used to add smoothie to cart
     */
    private fun addSmoothieToCart() {

        smoothie?.also { smoothie ->
            user?.also { user ->
                // Get quantity
                val quantityInCart = binding.quantity.text.toString().toInt()

                // If smoothie is added to cart, don't add again. If you want increase quantity,
                // you can go to cart and increase it
                viewModel.isExistedSmoothieInCart(user.uid, smoothie.id)
                    .observe(this.viewLifecycleOwner) { isExisted ->
                        if (isExisted) {
                            Toast.makeText(
                                requireContext(),
                                "${smoothie.name} is added to your cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Add smoothie to cart
                            viewModel.addSmoothieToCart(user.uid, smoothie.id, quantityInCart)
                            // Reduce quantityInStock
                            viewModel.updateQuantityInStock(smoothie.id, quantityInCart)
                        }
                    }
            }
        }
    }
}