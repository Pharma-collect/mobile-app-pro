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

    private lateinit var prescriptionViewModel: PrescriptionViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        prescriptionViewModel =
                ViewModelProvider(this).get(PrescriptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_prescription, container, false)

        val current: TextView = root.findViewById(R.id.pharma_name_pres)
        val id: TextView = root.findViewById(R.id.user_id_pres)
        val order: TextView = root.findViewById(R.id.pharma_current_order_pres)
        val waiting: TextView = root.findViewById(R.id.to_do_layout_tv_waiting)
        val doing: TextView = root.findViewById(R.id.in_progress_layout_tv_waiting)
        val done: TextView = root.findViewById(R.id.done_layout_tv_amount)
        val all: TextView = root.findViewById(R.id.all_layout_tv_amount)

        prescriptionViewModel.currentUser.observe(viewLifecycleOwner, Observer { current.text = it })
        prescriptionViewModel.idUser.observe(viewLifecycleOwner, Observer { id.text = it })
        prescriptionViewModel.orderCurrent.observe(viewLifecycleOwner, Observer { order.text = it })
        prescriptionViewModel.pending.observe(viewLifecycleOwner, Observer { waiting.text = it })
        prescriptionViewModel.container.observe(viewLifecycleOwner, Observer { doing.text = it })
        prescriptionViewModel.finish.observe(viewLifecycleOwner, Observer { done.text = it })
        prescriptionViewModel.ready.observe(viewLifecycleOwner, Observer { all.text = it })

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
            R.id.to_do_layout_card_view -> navController.navigate(R.id.action_navigation_prescription_to_choiceTypeFragment)
        }
    }
}