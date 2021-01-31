package fr.isen.emelian.pharma_collect_pro.ui.prescription.containerOrders

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.emelian.pharma_collect_pro.R

class ContainerOrderFragment : Fragment() {

    companion object {
        fun newInstance() = ContainerOrderFragment()
    }

    private lateinit var viewModel: ContainerOrderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_container_order, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContainerOrderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}