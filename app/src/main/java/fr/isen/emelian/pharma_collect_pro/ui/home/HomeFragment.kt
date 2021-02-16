package fr.isen.emelian.pharma_collect_pro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.appbar.CollapsingToolbarLayout
import fr.isen.emelian.pharma_collect_pro.R

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Declaration of ui elements
        val textViewCurrent: TextView = root.findViewById(R.id.pharma_name_home)
        val textViewId: TextView = root.findViewById(R.id.user_id_header)
        val textViewOrder: TextView = root.findViewById(R.id.pharma_current_order)
        val textViewPrescription: TextView = root.findViewById(R.id.prescription_layout_tv_waiting)
        val textViewLocker: TextView = root.findViewById(R.id.locker_layout_tv_waiting)
        val collapsingToolbarLayout: CollapsingToolbarLayout = root.findViewById(R.id.collapse_bar)

        // Observable of ui elements
        homeViewModel.current.observe(viewLifecycleOwner, Observer { textViewCurrent.text = it })
        homeViewModel.id.observe(viewLifecycleOwner, Observer { textViewId.text = it })
        homeViewModel.order.observe(viewLifecycleOwner, Observer { textViewOrder.text = it })
        homeViewModel.prescription.observe(viewLifecycleOwner, Observer { textViewPrescription.text = it })
        homeViewModel.locker.observe(viewLifecycleOwner, Observer { textViewLocker.text = it })
        homeViewModel.name.observe(viewLifecycleOwner, Observer { collapsingToolbarLayout.title = it })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        // Instantiation of navigation elements
        view.findViewById<CardView>(R.id.prescription_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.shop_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.locker_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.pharma_layout_card_view).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.pharma_user_info).setOnClickListener(this)
        view.findViewById<CardView>(R.id.product_layout_card_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Setting up actions on click on elements
            R.id.prescription_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_prescription)
            R.id.shop_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_shop)
            R.id.locker_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_locker)
            R.id.pharma_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_pharmacy)
            R.id.pharma_user_info -> navController.navigate(R.id.action_navigation_home_to_userProfileFragment)
            R.id.product_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_product)
        }
    }
}