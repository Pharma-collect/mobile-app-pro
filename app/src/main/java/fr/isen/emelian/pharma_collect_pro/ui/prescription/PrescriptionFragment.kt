package fr.isen.emelian.pharma_collect_pro.ui.prescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R

class PrescriptionFragment : Fragment(), View.OnClickListener {

    private lateinit var notificationsViewModel: PrescriptionViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProvider(this).get(PrescriptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_prescription, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<CardView>(R.id.to_do_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.in_progress_layout_card_view).setOnClickListener(this)
        view.findViewById<CardView>(R.id.done_layout_card_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.to_do_layout_card_view -> navController.navigate(R.id.action_navigation_prescription_to_navigation_to_do)
            R.id.in_progress_layout_card_view -> navController.navigate(R.id.action_navigation_prescription_to_navigation_in_progress)
            R.id.done_layout_card_view -> navController.navigate(R.id.action_navigation_prescription_to_navigation_done)
        }
    }
}