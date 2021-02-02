package fr.isen.emelian.pharma_collect_pro.ui.prescription.finishOrders

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import java.math.BigDecimal

class FinishOrderFragment : Fragment() , View.OnClickListener {

    private lateinit var finishOrderViewModel: FinishOrderViewModel

    private lateinit var id_order: IDs
    private lateinit var navController: NavController
    private lateinit var order_id: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        finishOrderViewModel = ViewModelProvider(this).get(FinishOrderViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_container_order, container, false)

        val orderID: TextView = root.findViewById(R.id.id_order)
        val preparator : TextView = root.findViewById(R.id.preparator)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val locker: TextView = root.findViewById(R.id.locker_nb)

        finishOrderViewModel.orderID.observe(viewLifecycleOwner, Observer { orderID.text = it })
        finishOrderViewModel.clientID.observe(viewLifecycleOwner, Observer { clientID.text = it })
        finishOrderViewModel.statusOrder.observe(viewLifecycleOwner, Observer { statusOrder.text = it })
        finishOrderViewModel.detailText.observe(viewLifecycleOwner, Observer { detailText.text = it })
        finishOrderViewModel.preparator.observe(viewLifecycleOwner, Observer { preparator.text = it })
        finishOrderViewModel.locker.observe(viewLifecycleOwner, Observer { locker.text = it })

        order_id = id_order.id.toString()
        finishOrderViewModel.idOrder = order_id

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_client_info).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_client_info -> switchToClientInfo()
            R.id.button_back -> activity?.onBackPressed()
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_finishOrderFragment_to_detailClientFragment, bundle)
    }

}