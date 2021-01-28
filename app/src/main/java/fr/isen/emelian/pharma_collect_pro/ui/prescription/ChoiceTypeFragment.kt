package fr.isen.emelian.pharma_collect_pro.ui.prescription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R


class ChoiceTypeFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choice_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<CardView>(R.id.prescription_side).setOnClickListener(this)
        view.findViewById<CardView>(R.id.shop_side).setOnClickListener(this)
        view.findViewById<Button>(R.id.back_choice_button).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.prescription_side -> navController.navigate(R.id.action_choiceTypeFragment_to_pendingPresFragment)
            R.id.shop_side -> navController.navigate(R.id.action_choiceTypeFragment_to_pendingOrderFragment)
            R.id.back_choice_button -> activity?.onBackPressed()
        }
    }

}