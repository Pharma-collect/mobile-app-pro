package fr.isen.emelian.pharma_collect_pro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
       // val textView: TextView = root.findViewById(R.id.tvWelcomeBack)
        /*homeViewModel.welcomeText.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<CardView>(R.id.prescription_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.shop_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.locker_layout_card_view).setOnClickListener(this)
        //view.findViewById<CardView>(R.id.client_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.pharma_layout_card_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.prescription_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_prescription)
            R.id.shop_layout_card_view -> navController?.navigate(R.id.action_navigation_home_to_navigation_prescription)
            R.id.locker_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_locker)
            //R.id.client_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_history)
            R.id.pharma_layout_card_view -> navController.navigate(R.id.action_navigation_home_to_navigation_pharmacy)
        }
        //To make a return
        //R.id.... -> activity?.onBackPressed
    }
}