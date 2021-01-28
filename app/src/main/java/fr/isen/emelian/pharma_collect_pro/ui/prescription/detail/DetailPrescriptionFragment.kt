package fr.isen.emelian.pharma_collect_pro.ui.prescription.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.repository.OrderRepository
import java.math.BigDecimal

class DetailPrescriptionFragment : Fragment(), View.OnClickListener {

    private lateinit var detailPrescriptionViewModel: DetailPrescriptionViewModel

    private lateinit var id_order: IDs
    private lateinit var id_prescription: IDs
    private lateinit var navController: NavController
    private val orderRepository: OrderRepository =
        OrderRepository()
    private lateinit var order_id: String
    private lateinit var prescription_id: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
        id_prescription = arguments!!.getParcelable("prescription_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        detailPrescriptionViewModel = ViewModelProvider(this).get(DetailPrescriptionViewModel::class.java)
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail_prescription, container, false)

        order_id = id_order.id.toString()
        prescription_id = id_prescription.toString()

        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val totalPrice: TextView = root.findViewById(R.id.total_price)
        val prescriptionImage: ImageView = root.findViewById(R.id.prescription_image_view)

        detailPrescriptionViewModel.orderID.observe(viewLifecycleOwner, Observer { orderID.text = it })
        detailPrescriptionViewModel.clientID.observe(viewLifecycleOwner, Observer { clientID.text = it })
        detailPrescriptionViewModel.statusOrder.observe(viewLifecycleOwner, Observer { statusOrder.text = it })
        detailPrescriptionViewModel.detailText.observe(viewLifecycleOwner, Observer { detailText.text = it })
        detailPrescriptionViewModel.totalPrice.observe(viewLifecycleOwner, Observer { totalPrice.text = it })
        //detailPrescriptionViewModel.pre

        detailPrescriptionViewModel.idOrder = order_id
        detailPrescriptionViewModel.idPrescription = prescription_id

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
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_detailPrescriptionFragment_to_detailClientFragment, bundle)
    }
}