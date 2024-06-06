package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.os.Bundle
import android.util.Log
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
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.Smoothie
import com.example.smoothieshopapp.data.network.SmoothieApi
import com.example.smoothieshopapp.data.repository.SmoothieRepository
import com.example.smoothieshopapp.data.repository.UserRepository
import com.example.smoothieshopapp.databinding.FragmentDetailSmoothieBinding
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SpecialitiesSmoothieAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.example.smoothieshopapp.util.findNavControllerSafely
import com.example.smoothieshopapp.util.loadImageWithImageUrl

class DetailSmoothieFragment : Fragment() {

    // Arguments of DetailSmoothie in nav_graph
    private val args: DetailSmoothieFragmentArgs by navArgs()

    // Binding to fragment_detail_smoothie
    private lateinit var binding: FragmentDetailSmoothieBinding

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(UserRepository(), SmoothieRepository())
    }

    // Current user
    private var uid: String? = SmoothieApi.firebaseAuth.currentUser?.uid
    private var favorites: List<Smoothie>? = null
    private var carts: List<Cart>? = null

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

        //Get smoothie by id
        viewModel.getSmoothieById(
            args.id,
            onSuccess = { smoothie ->
                this.smoothie = smoothie
                bind()
            },
            onFailure = {
                Log.d("Test Smoothie", "onViewCreated: $it")
            }
        )

        //SubmitList for recommend products recycler view
        viewModel.getRandomSmoothies(
            onSuccess = {
                adapter?.submitList(it)
            },
            onFailure = {
                Log.d("Test Smoothie", "onViewCreated: $it")
            }
        )
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
            if (quantity > 1) {
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
    private fun bind() {
        //Todo set smoothie information

        smoothie?.also { smoothie ->
            // Set name smoothie
            binding.name.text = smoothie.name
            // Load image
            binding.image.loadImageWithImageUrl(smoothie.imageUrl)

            // Set rating
            binding.ratingBar.rating = smoothie.rating
            // Set price
            binding.price.text = String.format("%.2f$", smoothie.price)

            bindFavorite(smoothie)

            uid?.let {
                viewModel.getCart(
                    uid = it,
                    onSuccess = { carts ->
                        this.carts = carts

                        val inCart = carts.map { cart -> cart.smoothie.id }.contains(smoothie.id)
                        binding.btnSubmit.isEnabled = !inCart
                        binding.btnSubmit.text = if (inCart) {
                            getString(R.string.added_to_cart)
                        } else {
                            getString(R.string.btnSubmitText)
                        }
                    },
                    onFailure = {
                        Log.d("Test Smoothie", "bind: $it")
                    })
            }
        }
    }

    private fun bindFavorite(smoothie: Smoothie) {
        viewModel.getFavorites(
            onSuccess = { smoothies ->
                favorites = smoothies

                val isFavorite = smoothies?.map { it.id }?.contains(smoothie.id) ?: false
                // Set drawable favorite
                binding.btnLike.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        if (isFavorite) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24,
                        null
                    )
                )
                binding.btnLike.setOnClickListener {
                    if (isFavorite) {
                        viewModel.removeFavorite(
                            smoothie,
                            favorites ?: listOf(),
                            onSuccess = {
                                bindFavorite(smoothie)
                            },
                            onFailure = {
                                Log.d("Test Smoothie", "bind: $it")
                            })
                    } else {
                        viewModel.setFavorite(
                            smoothie,
                            favorites ?: listOf(),
                            onSuccess = {
                                bindFavorite(smoothie)
                            },
                            onFailure = {
                                Log.d("Test Smoothie", "bind: $it")
                            })
                    }
                }

            },
            onFailure = {
                Log.d("Test Smoothie", "bind: $it")
            }
        )
    }

    /**
     * This function is used to add smoothie to cart
     */
    private fun addSmoothieToCart() {
        if (uid == null) {
            Toast.makeText(
                requireContext(),
                "Need login to add smoothies to your cart!!!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val smoothie = this.smoothie ?: return
        val quantity = binding.quantity.text.toString().toIntOrNull() ?: return
        val carts = this.carts ?: return

        viewModel.addToCart(
            uid!!,
            cart = Cart(
                smoothie.copy(quantityInStock = smoothie.quantityInStock - quantity),
                quantity
            ),
            carts,
            onSuccess = {
                Toast.makeText(requireContext(), "Add to cart successful!", Toast.LENGTH_SHORT)
                    .show()
                findNavControllerSafely()?.navigate(R.id.cartFragment)
            },
            onFailure = {
                Log.d("Test Smoothie", "addSmoothieToCart: $it")
            }
        )
    }
}