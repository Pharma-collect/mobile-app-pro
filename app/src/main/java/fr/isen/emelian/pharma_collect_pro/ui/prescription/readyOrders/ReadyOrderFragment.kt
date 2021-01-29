package fr.isen.emelian.pharma_collect_pro.ui.prescription.readyOrders

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
import fr.isen.emelian.pharma_collect_pro.ui.prescription.pendingOrders.DetailOrderViewModel
import java.math.BigDecimal

class ReadyOrderFragment : Fragment(), View.OnClickListener  {

    private lateinit var id_order: IDs
    private lateinit var navController: NavController
    private lateinit var order_id: String

    private lateinit var viewModel: ReadyOrderViewModel

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(ReadyOrderViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_ready_order, container, false)

        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val totalPrice: TextView = root.findViewById(R.id.total_price)
        val preparator: TextView = root.findViewById(R.id.id_preparator)

        viewModel.orderID.observe(viewLifecycleOwner, Observer { orderID.text = it })
        viewModel.clientID.observe(viewLifecycleOwner, Observer { clientID.text = it })
        viewModel.statusOrder.observe(viewLifecycleOwner, Observer { statusOrder.text = it })
        viewModel.detailText.observe(viewLifecycleOwner, Observer { detailText.text = it })
        viewModel.totalPrice.observe(viewLifecycleOwner, Observer { totalPrice.text = it })
        viewModel.preparator.observe(viewLifecycleOwner, Observer { preparator.text = it })

        order_id = id_order.id.toString()
        viewModel.idOrder = order_id

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_client_info).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_locker).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_client_info -> switchToClientInfo()
            R.id.button_back -> activity?.onBackPressed()
            R.id.button_locker -> navController.navigate(R.id.action_readyOrderFragment_to_selectLockerFragment)
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_readyOrderFragment_to_detailClientFragment, bundle)
    }

}