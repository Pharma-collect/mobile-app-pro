package fr.isen.emelian.pharma_collect_pro.ui.prescription.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.OrderRepository
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class DetailPrescriptionFragment : Fragment(), View.OnClickListener {

    private lateinit var id_order: IDs
    private lateinit var navController: NavController
    private val orderRepository: OrderRepository =
            OrderRepository()
    private lateinit var client: String
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User =
            User()
    private lateinit var order_id: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_order = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail_prescription, container, false)


        order_id = id_order.id.toString()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        Log.d("PharmaInfo", response.toString())
                        if (jsonResponse["success"] == true) {
                            var data = JSONObject(jsonResponse.get("result").toString())
                            val orderID: TextView = root.findViewById(R.id.id_order)
                            val clientID: TextView = root.findViewById(R.id.id_client)
                            val statusOrder: TextView = root.findViewById(R.id.status_order)
                            val detailText: TextView = root.findViewById(R.id.detail_text)
                            val totalPrice: TextView = root.findViewById(R.id.total_price)

                            orderID.text = "ID : " + data["id"]
                            clientID.text = "Client id : " + data["id_client"]
                            statusOrder.text = "Current status : " + data["status"]
                            detailText.text = data["detail"].toString()
                            totalPrice.text = "Total price : " + data["total_price"]

                            client = data["id_client"].toString()

                        }else{

                            Toast.makeText(context, "Error while getting order info", Toast.LENGTH_LONG).show()

                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                    }
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Host"] = "node"
                        params["Authorization"] = myUser.token.toString()
                        return params
                    }
                    override fun getParams(): MutableMap<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["order_id"] = order_id
                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

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

    fun switchToClientInfo() {
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_detailPrescriptionFragment_to_detailClientFragment, bundle)
    }
}