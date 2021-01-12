package fr.isen.emelian.pharma_collect_pro.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.appbar.CollapsingToolbarLayout
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.SplashActivity
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView_current: TextView = root.findViewById(R.id.pharma_name_home)
        val textView_id: TextView = root.findViewById(R.id.user_id_header)
        val textView_order: TextView = root.findViewById(R.id.pharma_current_order)
        val textView_prescription: TextView = root.findViewById(R.id.prescription_layout_tv_waiting)
        val textView_locker: TextView = root.findViewById(R.id.locker_layout_tv_waiting)
        val collapsingToolbarLayout: CollapsingToolbarLayout = root.findViewById(R.id.collapse_bar)

        homeViewModel.current.observe(viewLifecycleOwner, Observer { textView_current.text = it })
        homeViewModel.id.observe(viewLifecycleOwner, Observer { textView_id.text = it })
        homeViewModel.order.observe(viewLifecycleOwner, Observer { textView_order.text = it })
        homeViewModel.prescription.observe(viewLifecycleOwner, Observer { textView_prescription.text = it })
        homeViewModel.locker.observe(viewLifecycleOwner, Observer { textView_locker.text = it })
        homeViewModel.name.observe(viewLifecycleOwner, Observer { collapsingToolbarLayout.title = it })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<CardView>(R.id.prescription_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.shop_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.locker_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.pharma_layout_card_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.prescription_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_prescription)
            R.id.shop_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_shop)
            R.id.locker_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_locker)
            R.id.pharma_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_pharmacy)
        }
    }
}