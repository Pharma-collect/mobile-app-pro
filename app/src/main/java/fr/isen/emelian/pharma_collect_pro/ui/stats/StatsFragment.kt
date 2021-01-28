package fr.isen.emelian.pharma_collect_pro.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R


class StatsFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var viewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatsViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_pie_order).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_barchart_product).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_pie_locker).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_pie_user_pro).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.button_pie_order -> navController.navigate(R.id.action_navigation_shop_to_orderGraphFragment)
            R.id.button_barchart_product -> navController.navigate(R.id.action_navigation_shop_to_productGraphFragment)
            R.id.button_pie_locker -> navController.navigate(R.id.action_navigation_shop_to_lockerGraphFragment)
            R.id.button_pie_user_pro -> navController.navigate(R.id.action_navigation_shop_to_userProGraphFragment)
        }
    }
}