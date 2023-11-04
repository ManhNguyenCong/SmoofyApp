package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentCartBinding
import com.example.smoothieshopapp.model.Cart
import com.example.smoothieshopapp.model.priceFormatted
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SmoothiePayAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.example.smoothieshopapp.util.loadImageWithImageUrl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class CartFragment : Fragment() {

    // Binding to fragment_cart
    private lateinit var binding: FragmentCartBinding

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(SmoothieApi.dbRef)
    }

    // SmoothiePayAdapter
    private var smoothiePayAdapter: SmoothiePayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Set auto scroll toolbar
        autoScrollToolbar()

        // Set up recycler view
        setupRecyclerView()

        // Set event choose payment method
        binding.paymentMethod.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.paymentMethod, Gravity.CENTER)

            val paymentMethods = resources.getStringArray(R.array.paymentMethods)
            popupMenu.menu.add(paymentMethods[1])
            popupMenu.menu.add(paymentMethods[2])

            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.title in paymentMethods) {
                    binding.paymentMethod.text = menuItem.title
                    true
                } else {
                    false
                }
            }

            popupMenu.show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser

        user?.also {
            viewModel.getSmoothiesInCart(it.uid).observe(this.viewLifecycleOwner) { cart ->
                bind(cart)
            }
        }
    }

    /**
     * This function is used to setup smoothie pay recyclerview
     */
    private fun setupRecyclerView() {
        // Init smoothie pay adapter
        smoothiePayAdapter = SmoothiePayAdapter(
            displaySmoothieById = { smoothieId, txtViewName, txtViewPrice, imgView ->
                viewModel.getSmoothieById(smoothieId).observe(this.viewLifecycleOwner) { smoothie ->
                    txtViewName.text = smoothie.name
                    txtViewPrice.text = smoothie.priceFormatted()
                    imgView.loadImageWithImageUrl(smoothie.imageUrl)
                }
            },
            onClickBtnIncrease = { userId, smoothieId, quantity ->
                viewModel.updateQuantityInCart(userId, smoothieId, quantity + 1)
                viewModel.updateQuantityInStock(smoothieId, -1)
            },
            onClickBtnReduce = { userId, smoothieId, quantity ->
                viewModel.updateQuantityInCart(userId, smoothieId, quantity - 1)
                viewModel.updateQuantityInStock(smoothieId, 1)
            },
            onClickBtnRemove = { userId, smoothieId, quantity ->
                viewModel.removeSmoothieFromCart(userId, smoothieId)
                viewModel.updateQuantityInStock(smoothieId, quantity)
            }
        )
        // Set adapter for recycler view
        binding.smoothieRecyclerView.adapter = smoothiePayAdapter
    }

    /**
     * This function is used to bind data for recycler view and calculate total cost
     *
     * @param carts
     */
    private fun bind(carts: List<Cart>) {
        // Submit list for smoothiePayAdapter
        smoothiePayAdapter?.also {
            it.submitList(carts)
        }

        // Map of cost for each smoothie in cart,
        // use Map, because when observe a livedata, it can be repeated, so
        // need a key to don't get again cost of a smoothie
        val costs: MutableMap<String, Float> = mutableMapOf()

        carts.forEach { cart ->
            val smoothieLiveData = viewModel.getSmoothieById(cart.smoothieId)

            smoothieLiveData.observe(this.viewLifecycleOwner) { smoothie ->
                // Add cost of a smoothie in cart to map with key is it's id
                if (!costs.contains(smoothie.id)) {
                    costs[smoothie.id] = smoothie.price * cart.quantity
                }
                // Calculate total price
                val totalPrice = costs.map { (_, value) ->
                    value
                }.sum()
                // Set text for total price
                binding.totalPrice.text = String.format("%.2f$", totalPrice)

                // Remove observer after calculate price complete
                smoothieLiveData.removeObservers(this.viewLifecycleOwner)
            }
        }

        if (binding.paymentMethod.text.isEmpty()) {
            binding.paymentMethod.text = resources.getStringArray(R.array.paymentMethods)[0]
        }
    }

    /**
     * This function is used to scroll toolbar when scroll main content of fragment
     */
    private fun autoScrollToolbar() {
        // Get range which toolbar can scroll (0..scrollRange)
        val scrollRange = binding.fragmentTitle.paddingTop - binding.fragmentTitle.paddingBottom

        binding.scrollView.setOnScrollChangeListener { _, _, y, _, oldY ->
            if (y <= scrollRange) {
                // If scroll y in scroll range, scroll toolbar to y
                binding.toolbar.scrollTo(0, y)
            } else if (oldY < scrollRange) {
                // If old y in scroll range and it scroll out scrollRange after,
                // scroll toolbar to scrollRange
                binding.toolbar.scrollTo(0, scrollRange)
            }
        }
    }
}