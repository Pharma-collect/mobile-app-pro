package fr.isen.emelian.pharma_collect_pro.ui.prescription.pendingOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class DetailPrescriptionFragment : Fragment(), View.OnClickListener {

    private lateinit var detailPrescriptionViewModel: DetailPrescriptionViewModel

    private lateinit var id_order: IDs
    //private lateinit var id_prescription: IDs
    private lateinit var navController: NavController
    //private val orderRepository: OrderRepository = OrderRepository()
    private val orderRepository: OrderRepository = OrderRepository()
    private lateinit var order_id: String
    private val myUser: User = User()
    //private lateinit var prescription_id: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        detailPrescriptionViewModel = ViewModelProvider(this).get(DetailPrescriptionViewModel::class.java)
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail_prescription, container, false)

        order_id = id_order.id.toString()
        //prescription_id = id_prescription.toString()

        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val totalPrice: TextView = root.findViewById(R.id.total_price)
        //val prescriptionImage: ImageView = root.findViewById(R.id.prescription_image_view)

        detailPrescriptionViewModel.orderID.observe(viewLifecycleOwner, Observer { orderID.text = it })
        detailPrescriptionViewModel.clientID.observe(viewLifecycleOwner, Observer { clientID.text = it })
        detailPrescriptionViewModel.statusOrder.observe(viewLifecycleOwner, Observer { statusOrder.text = it })
        detailPrescriptionViewModel.detailText.observe(viewLifecycleOwner, Observer { detailText.text = it })
        detailPrescriptionViewModel.totalPrice.observe(viewLifecycleOwner, Observer { totalPrice.text = it })

        detailPrescriptionViewModel.idOrder = order_id
        //detailPrescriptionViewModel.idPrescription = prescription_id

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
        navController.navigate(R.id.action_detailPrescriptionFragment_to_detailClientFragment, bundle)
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
            } else {
                context?.let { it1 -> orderRepository.updateOrderToReady(order_id, "ready", myUser.id.toString(), "RAS", it1) }
            }

            alertDialog.dismiss()
            navController.navigate((R.id.action_detailPrescriptionFragment_to_navigation_prescription))
        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }
}