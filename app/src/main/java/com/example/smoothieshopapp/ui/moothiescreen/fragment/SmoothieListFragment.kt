package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentSmoothieListBinding
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SmoothieAdapter
import com.example.smoothieshopapp.ui.moothiescreen.adapter.SpecialitiesSmoothieAdapter
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.example.smoothieshopapp.util.findNavControllerSafely
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser

/**
 * This fragment is used to show list of smoothies
 *
 *
 */
class SmoothieListFragment : Fragment() {
    // Binding to fragment_smoothie_list
    private lateinit var binding: FragmentSmoothieListBinding

    // Current user
    private var user: FirebaseUser? = null

    // View model
    private val viewModel: SmoothieViewModel by activityViewModels {
        SmoothieViewModelFactory(SmoothieApi.dbRef)
    }

    // Init specialities smoothie adapter
    private var specialityAdapter: SpecialitiesSmoothieAdapter? = null

    // Init smoothie adapter
    private var smoothieAdapter: SmoothieAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentSmoothieListBinding.inflate(layoutInflater)

        // When open app, bottom navigation will be gone, so need display it again
        activity?.findViewById<View>(R.id.mainContent)
            ?.findViewById<BottomNavigationView>(R.id.bottomNavView)
            ?.let {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                }
            }

        // Set auto scroll toolbar
        autoScrollToolbar()

        // Set open/close nav view
        setNavigationView()

        // Set up RecyclerViews
        setSpecialitiesRecyclerView()
        setAllProductsRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user
        user = SmoothieApi.firebaseAuth.currentUser

        // Binding data for RecyclerViews
        bindSpecialitiesRecyclerView()
        bindAllProductsRecyclerView()
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
     * This function is used to set up adapter for allProductsRecyclerView
     */
    private fun setAllProductsRecyclerView() {
        smoothieAdapter = SmoothieAdapter(
            showDetailSmoothie = { id ->
                // Navigation to detail smoothie fragment
                val action =
                    SmoothieListFragmentDirections.actionSmoothieListFragmentToDetailSmoothieFragment(
                        id
                    )
                findNavControllerSafely()?.navigate(action)
            },
            setFavoriteSmoothie = { smoothieId, imgView ->
                // Set current favorite status
                user?.also { user ->
                    viewModel.getAllFavoriteByUserId(user.uid)
                        .observe(this.viewLifecycleOwner) {
                            if (smoothieId in it) {
                                // Set drawable favorite
                                imgView.setImageDrawable(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_round_favorite_24,
                                        null
                                    )
                                )
                                // Event remove favorite smoothie
                                imgView.setOnClickListener {
                                    viewModel.removeFavorite(
                                        user.uid,
                                        smoothieId
                                    )
                                }
                            } else {
                                // Set drawable don't favorite
                                imgView.setImageDrawable(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_round_favorite_border_24,
                                        null
                                    )
                                )
                                // Set event add favorite smoothie
                                imgView.setOnClickListener {
                                    viewModel.addFavorite(
                                        user.uid,
                                        smoothieId
                                    )
                                }
                            }
                        }
                }
            })

        binding.allProductsRecyclerView.adapter = smoothieAdapter
    }

    /**
     * This function is used to set up adapter for specialitiesRecyclerView
     */
    private fun setSpecialitiesRecyclerView() {
        specialityAdapter = SpecialitiesSmoothieAdapter { id ->
            val action =
                SmoothieListFragmentDirections.actionSmoothieListFragmentToDetailSmoothieFragment(id)
            findNavControllerSafely()?.navigate(action)
        }

        binding.specialitiesRecyclerView.adapter = specialityAdapter
    }

    /**
     * This function is used to summit list for specialityAdapter
     */
    private fun bindSpecialitiesRecyclerView() {
        // Todo summitList for specialityAdapter
        viewModel.getAllSmoothies().observe(this.viewLifecycleOwner) { smoothies ->
            specialityAdapter?.also {
                it.submitList(
                    smoothies.filter { smoothie ->
                        smoothie.rating >= 4
                    }.sortedByDescending { smoothie -> smoothie.rating }
                )
            }
        }
    }

    /**
     * This function is used to summit list for smoothieAdapter
     */
    private fun bindAllProductsRecyclerView() {
        // Todo summitList for specialityAdapter
        viewModel.getAllSmoothies().observe(this.viewLifecycleOwner) { smoothies ->
            smoothieAdapter?.also {
                it.submitList(
                    smoothies.sortedBy { smoothie -> smoothie.name }
                )
            }
        }
    }
}