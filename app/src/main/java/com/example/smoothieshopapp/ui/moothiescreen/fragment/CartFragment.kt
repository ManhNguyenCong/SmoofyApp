package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.app.AlertDialog
import android.app.Dialog
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentBillDialogBinding
import com.example.smoothieshopapp.databinding.FragmentCartBinding
import com.example.smoothieshopapp.databinding.FragmentSetupUserInfoDialogBinding
import com.example.smoothieshopapp.model.Cart
import com.example.smoothieshopapp.model.NotificationData
import com.example.smoothieshopapp.model.NotificationPush
import com.example.smoothieshopapp.model.User
import com.example.smoothieshopapp.model.priceFormatted
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SmoothiePayAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.example.smoothieshopapp.util.loadImageWithImageUrl
import com.google.firebase.messaging.FirebaseMessaging

private const val TAG = "CartFragment"

class CartFragment : Fragment() {

    // Binding to fragment_cart
    private lateinit var binding: FragmentCartBinding

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(SmoothieApi.dbRef)
    }

    // SmoothiePayAdapter
    private var smoothiePayAdapter: SmoothiePayAdapter? = null

    // Current user
    private var user: User? = null

    // Get list products in cart as a string
    private var strProducts: String? = null

    // Smoothies in cart
    private var cart: List<Cart>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

        // Get current user
        SmoothieApi.firebaseAuth.currentUser?.also { firebaseUser ->
            viewModel.getUserInformation(firebaseUser.uid).observe(this.viewLifecycleOwner) {
                user = it
            }
        }

        // Get smoothies in cart
        SmoothieApi.firebaseAuth.currentUser?.also {
            viewModel.getSmoothiesInCart(it.uid).observe(this.viewLifecycleOwner) { cart ->
                this.cart = cart
                bind(cart)
            }
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
     * This function is used to set event on click of button checkout
     */
    private fun setOnButtonCheckoutClick() {
        // Even click button checkout
        binding.btnCheckout.setOnClickListener {
            user?.also { userInfo ->
                // If user don't enter some information, show set up user information
                if (userInfo.name.isEmpty() || userInfo.address.isEmpty() || userInfo.phoneNumber.isEmpty()) {
                    // Show set up user information
                    Toast.makeText(
                        requireContext(),
                        "Enter full your information, please!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Show dialog
                    SetUpUserInfoDialogFragment(
                        userInfo
                    ) { userName, address, phoneNumber ->
                        // Valid entry
                        if (viewModel.validUserInfoEntry(userName, address, phoneNumber)) {
                            // If correct, update them to db
                            viewModel.updateNewUserInformation(
                                userInfo.id,
                                userName,
                                address,
                                phoneNumber
                            )
                        } else {
                            // Notification
                            Toast.makeText(
                                requireContext(),
                                "You need fill full your information to order products!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.show(childFragmentManager, SetUpUserInfoDialogFragment.TAG)
                }

                // Check payment method
                if (binding.paymentMethod.text.toString() == resources.getStringArray(R.array.paymentMethods)[1]) {
                    // Direct payment
                    strProducts?.also { products ->
                        BillDialogFragment(
                            userInfo,
                            products,
                            binding.totalPrice.text.toString()
                        ) {
                            // Save order
                            pushBill()
                            // Notification wait admin confirm it
                            pushNotification(
                                "Create bill",
                                "Bill creation has been completed, please wait for the administrator to check and confirm."
                            )
                        }.show(childFragmentManager, BillDialogFragment.TAG)
                    }
                } else if (binding.paymentMethod.text.toString()
                    == resources.getStringArray(R.array.paymentMethods)[2]
                ) {
                    // Todo payment via
                    Toast.makeText(
                        requireContext(),
                        "Pay will be released in the future!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If don't select payment method, disable button checkout
                    Toast.makeText(
                        requireContext(),
                        "You need select payment method before checkout!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
                if (menuItem.title in paymentMethods) {
                    binding.paymentMethod.text = menuItem.title
                    true
                } else {
                    false
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

            // Map of cost for each smoothie in cart,
            // use Map, because when observe a livedata, it can be repeated, so
            // need a key to don't get again cost of a smoothie
            val costs: MutableMap<String, Float> = mutableMapOf()
            // Map name of smoothies
            val names = mutableSetOf<String>()

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

                    // Get list product as a string
                    names.add(cart.quantity.toString() + " x " + smoothie.name)
                    strProducts = names.joinToString("\n")

                    // Remove observer after calculate price complete
                    smoothieLiveData.removeObservers(this.viewLifecycleOwner)
                }
            }
        }
    }

    /**
     * This function is used to push notification when checkout successful
     */
    private fun pushNotification(title: String, message: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(
                    TAG,
                    "setOnButtonCheckoutClick: " + task.exception
                )
                return@addOnCompleteListener
            }
            // Get token
            val token = task.result
            // Send notification
            viewModel.sendNotification(
                NotificationPush(
                    NotificationData(title, message),
                    token
                )
            )
        }
    }

    /**
     * This function is used to push bill and remove cart
     */
    private fun pushBill() {
        user?.also { user ->
            cart?.also { cart ->
                // Add bill
                viewModel.addBill(userId = user.id, cart)
                // Remove cart
                viewModel.removeSmoothiesInCart(user.id)
            }
        }
    }
}

/**
 * This class is used to create a setup user info dialog
 *
 * @param user current user information
 * @param saveNewUserInfo
 */
class SetUpUserInfoDialogFragment(
    private val user: User,
    private val saveNewUserInfo: (String, String, String) -> Unit
) : DialogFragment() {

    private lateinit var binding: FragmentSetupUserInfoDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Create dialog builder
            val builder = AlertDialog.Builder(context)
            // Inflate layout for dialog
            val dialogLayout =
                layoutInflater.inflate(R.layout.fragment_setup_user_info_dialog, null)
            // Binding to fragment_setup_user_info_dialog
            binding = FragmentSetupUserInfoDialogBinding.bind(dialogLayout)
            // Set variable "userInfor" in fragment
            binding.userInfo = user

            // Set layout for dialog
            builder.setView(dialogLayout)
                .setTitle(getString(R.string.setupUserInfoDialogTitle))
                .setPositiveButton(getString(R.string.oke)) { _, _ ->
                    // Set event positive button is clicked
                    saveNewUserInfo(
                        binding.txtName.text.toString(),
                        binding.txtAddress.text.toString(),
                        binding.txtPhoneNumber.text.toString()
                    )
                }

            // Create dialog
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "SetUpUserInfoDialog"
    }
}

/**
 * This class is used to setup a bill dialog fragment
 * which show user info and products want to payment
 *
 * @param user
 * @param products They have template is "name x quantity" and split by "\n"
 * @param totalPrice It have template "total$"
 * @param onPositiveButtonClick
 */
class BillDialogFragment(
    private val user: User,
    private val products: String,
    private val totalPrice: String,
    private val onPositiveButtonClick: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Create dialog builder
            val builder = AlertDialog.Builder(context)
            // Inflate layout for dialog fragment
            val dialogFragment = layoutInflater.inflate(R.layout.fragment_bill_dialog, null)
            // Binding to fragment_bill_dialog
            val binding = FragmentBillDialogBinding.bind(dialogFragment)
            // Load user info
            user.also {
                binding.userName.text = it.name
                binding.phoneNumber.text = it.phoneNumber
                binding.address.text = it.address
            }
            // Load products
            binding.products.text = products
            // Load total price
            binding.totalPrice.text = totalPrice
            // Set layout for dialog
            builder.setView(dialogFragment)
                .setPositiveButton(getString(R.string.oke)) { _, _ ->
                    // Confirm payment event
                    onPositiveButtonClick()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "BillDialog"
    }
}