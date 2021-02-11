package fr.isen.emelian.pharma_collect_pro.ui.prescription.orderMaking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.isen.emelian.pharma_collect_pro.R

class OrderMakingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view: View = inflater.inflate(R.layout.fragment_order_making, container, false)


        return view
    }
}