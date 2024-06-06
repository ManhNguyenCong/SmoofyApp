package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.data.model.Bill
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.data.network.SmoothieApi
import com.example.smoothieshopapp.data.repository.SmoothieRepository
import com.example.smoothieshopapp.data.repository.UserRepository
import com.example.smoothieshopapp.databinding.FragmentCartBinding
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SmoothiePayAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import java.util.Calendar

private const val TAG = "Test Smoothie"

class CartFragment : Fragment() {

    // Binding to fragment_cart
    private lateinit var binding: FragmentCartBinding

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(UserRepository(), SmoothieRepository())
    }

    // SmoothiePayAdapter
    private var smoothiePayAdapter: SmoothiePayAdapter? = null

    // Current user
    private var uid: String? = SmoothieApi.firebaseAuth.uid

    // Get list products in cart as a string
    private var strProducts: String? = null

    // Smoothies in cart
    private var carts: List<Cart>? = null
    private var isDirectPayment: Boolean? = null

    override fun onAttach(context: Context) {
        if (uid == null) {
            AlertDialog.Builder(requireContext()).setTitle("Login")
                .setMessage("Login to open your cart!!!").setOnCancelListener {
                    findNavController().navigateUp()
                }.create().show()
        }

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Set auto scroll toolbar
        autoScrollToolbar()
        // Set open/close navigation view
        setNavigationView()
        // Set up recycler view
        setupRecyclerView()
        // Set up combo-box payment method
        setCbxPaymentMethod()
        // Set event onclick for button checkout
        setOnButtonCheckoutClick()
        // Set default total price text
        binding.totalPrice.text = getString(R.string.defaultTotalPriceText)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid?.let { uid ->
            viewModel.getCart(uid, onSuccess = { carts ->
                this.carts = carts
                bind(carts)
            }, onFailure = {
                Log.d(TAG, "onViewCreated: $it")
            })
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
     * This function is used to setup smoothie pay recyclerview
     */
    private fun setupRecyclerView() {
        // Init smoothie pay adapter
        smoothiePayAdapter = SmoothiePayAdapter(onClickBtnIncrease = { cart ->
            if (cart.quantity >= cart.smoothie.quantityInStock) {
                Toast.makeText(
                    requireContext(), "Quantity in stock no enough!!!", Toast.LENGTH_SHORT
                ).show()
                return@SmoothiePayAdapter
            }

            val carts = this.carts ?: return@SmoothiePayAdapter
            viewModel.updateCart(cart.copy(quantity = cart.quantity + 1), carts, onSuccess = {
                smoothiePayAdapter?.submitList(it)
                bind(it)
            }, onFailure = {
                Log.d(TAG, "setupRecyclerView: $it")
            })
        }, onClickBtnReduce = { cart ->

            if (cart.quantity <= 1) {
                Toast.makeText(
                    requireContext(), "Quantity can't be less 1!!!", Toast.LENGTH_SHORT
                ).show()
                return@SmoothiePayAdapter
            }

            val carts = this.carts ?: return@SmoothiePayAdapter
            viewModel.updateCart(cart.copy(quantity = cart.quantity - 1), carts, onSuccess = {
                smoothiePayAdapter?.submitList(it)
                bind(it)
            }, onFailure = {
                Log.d(TAG, "setupRecyclerView: $it")
            })
        }, onClickBtnRemove = { cart ->
            val carts = this.carts ?: return@SmoothiePayAdapter
            viewModel.removeCart(cart, carts, onSuccess = {
                smoothiePayAdapter?.submitList(it)
                bind(it)
            }, onFailure = {
                Log.d(TAG, "setupRecyclerView: $it")
            })
        })
        // Set adapter for recycler view
        binding.smoothieRecyclerView.adapter = smoothiePayAdapter
    }

    /**
     * This function is used to set event on click of button checkout
     */
    private fun setOnButtonCheckoutClick() {
        binding.btnCheckout.setOnClickListener {
            // Check payment method
            if (isDirectPayment == null) {
                // If don't select payment method, disable button checkout
                Toast.makeText(
                    requireContext(),
                    "You need select payment method before checkout!!!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!(isDirectPayment!!)) {
                // Todo payment via
                Toast.makeText(
                    requireContext(),
                    "Pay will be released in the future!!!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.getUserByUID(uid!!, onSuccess = { userInfo ->
                userInfo?.let {
                    // If user don't enter some information, show set up user information
                    if (it.name.isNullOrEmpty() || it.address.isNullOrEmpty() || it.phoneNumber.isNullOrEmpty()) {
                        SetUpUserInfoDialogFragment(it) { userName, address, phoneNumber ->
                            if (userName.isNullOrEmpty() || address.isNullOrEmpty() || phoneNumber.isNullOrEmpty()) {
                                // Notification
                                Toast.makeText(
                                    requireContext(),
                                    "You need fill full your information to order products!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.updateUserInfo(userName, address, phoneNumber, onSuccess = {
                                    Toast.makeText(
                                        requireContext(),
                                        "Upload user info successfull!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }, onFailure = {
                                    Log.d(TAG, "setOnButtonCheckoutClick: $it")
                                })
                            }
                        }.show(childFragmentManager, SetUpUserInfoDialogFragment.TAG)

                        return@let
                    }

                    // Direct payment
                    strProducts?.also { products ->
                        BillDialogFragment(
                            it, products, binding.totalPrice.text.toString()
                        ) {
                            // Save order
                            pushBill(it)
                        }.show(childFragmentManager, BillDialogFragment.TAG)
                    }
                }
            }, onFailure = {
                Log.d(TAG, "setOnButtonCheckoutClick: $it")
            })
        }
    }

    /**
     * This function is used to set combo-box payment method
     */
    private fun setCbxPaymentMethod() {
        if (binding.paymentMethod.text.isEmpty()) {
            binding.paymentMethod.text = resources.getStringArray(R.array.paymentMethods)[0]
        }
        // Set event choose payment method
        binding.paymentMethod.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.paymentMethod, Gravity.CENTER)

            val paymentMethods = resources.getStringArray(R.array.paymentMethods)
            popupMenu.menu.add(paymentMethods[1])
            popupMenu.menu.add(paymentMethods[2])

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    paymentMethods[0] -> {
                        isDirectPayment = null
                        binding.paymentMethod.text = paymentMethods[0]
                        true
                    }

                    paymentMethods[1] -> {
                        isDirectPayment = true
                        binding.paymentMethod.text = paymentMethods[1]
                        true
                    }

                    paymentMethods[2] -> {
                        isDirectPayment = false
                        binding.paymentMethod.text = paymentMethods[2]
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    /**
     * This function is used to bind data for recycler view and calculate total cost
     *
     * @param carts
     */
    private fun bind(carts: List<Cart>) {
        if (carts.isEmpty()) {
            binding.smoothieRecyclerView.visibility = View.GONE
            binding.txtEmptyCart.visibility = View.VISIBLE
            binding.totalPrice.text = getString(R.string.defaultTotalPriceText)
            binding.btnCheckout.isEnabled = false
        } else {
            binding.smoothieRecyclerView.visibility = View.VISIBLE
            binding.txtEmptyCart.visibility = View.GONE
            binding.btnCheckout.isEnabled = true

            // Submit list for smoothiePayAdapter
            smoothiePayAdapter?.also {
                it.submitList(carts)
            }

            strProducts =
                carts.map { "${it.smoothie.name} x ${it.quantity}" }.joinToString("\n")

            val totalPrice = carts.map { it.smoothie.price * it.quantity }.sum()
            binding.totalPrice.text = String.format("%.2f$", totalPrice)
        }
    }

    /**
     * This function is used to push bill and remove cart
     */
    private fun pushBill(user: User) {
        if (carts.isNullOrEmpty()) {
            return
        }

        val date = Calendar.getInstance().timeInMillis
        val id = "${user.id}$date"
        val bill = Bill(id, user, carts!!, date, 0)
        viewModel.addBill(bill, onSuccess = {
            Toast.makeText(requireContext(), "Order successful!", Toast.LENGTH_SHORT).show()
            bind(listOf())
        }, onFailure = { msg ->
            Log.d(TAG, "pushBill: $msg")
        })
    }
}