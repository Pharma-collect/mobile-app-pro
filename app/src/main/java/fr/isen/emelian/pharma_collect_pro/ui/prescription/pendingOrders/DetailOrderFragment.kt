package fr.isen.emelian.pharma_collect_pro.ui.prescription.pendingOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.OrderRepository
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class DetailOrderFragment : Fragment(), View.OnClickListener {

    private lateinit var detailOrderViewModel: DetailOrderViewModel

    private lateinit var id_order: IDs
    private lateinit var navController: NavController
    private val orderRepository: OrderRepository = OrderRepository()
    private lateinit var order_id: String
    private val myUser: User = User()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        detailOrderViewModel = ViewModelProvider(this).get(DetailOrderViewModel::class.java)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail_order, container, false)


        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        //val detailText: TextView = root.findViewById(R.id.detail_text)
        val totalPrice: TextView = root.findViewById(R.id.total_price)

        detailOrderViewModel.orderID.observe(viewLifecycleOwner, Observer { orderID.text = it })
        detailOrderViewModel.clientID.observe(viewLifecycleOwner, Observer { clientID.text = it })
        detailOrderViewModel.statusOrder.observe(viewLifecycleOwner, Observer { statusOrder.text = it })
        //detailOrderViewModel.detailText.observe(viewLifecycleOwner, Observer { detailText.text = it })
        detailOrderViewModel.totalPrice.observe(viewLifecycleOwner, Observer { totalPrice.text = it })

        order_id = id_order.id.toString()
        detailOrderViewModel.idOrder = order_id

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_client_info).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_treat).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_client_info -> switchToClientInfo()
            R.id.button_back -> activity?.onBackPressed()
            R.id.button_treat -> openDialog()
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_detailOrderFragment_to_detailClientFragment, bundle)
    }

    private fun openDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_set_ready, null)
        val editTextNote: EditText = navView.findViewById(R.id.not_ed)
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_confirm.setOnClickListener {

            val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
            if (datas.isNotEmpty()) {
                val jsonObject = JSONObject(datas)
                myUser.id = jsonObject.optInt("id")
            }
            val detail = editTextNote.text.toString()
            if(editTextNote.text.toString() != "") {
                context?.let { it1 -> orderRepository.updateOrderToReady(order_id, "ready", myUser.id.toString(), detail, it1) }
                Toast.makeText(context, "Order state successfully updated", LENGTH_LONG).show()
            } else {
                context?.let { it1 -> orderRepository.updateOrderToReady(order_id, "ready", myUser.id.toString(), "RAS", it1) }
                Toast.makeText(context, "Order state successfully updated", LENGTH_LONG).show()
            }

            Handler().postDelayed({
                alertDialog.dismiss()
                navController.navigate((R.id.action_detailOrderFragment_to_navigation_prescription))
            }, 1000)
        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }
}
