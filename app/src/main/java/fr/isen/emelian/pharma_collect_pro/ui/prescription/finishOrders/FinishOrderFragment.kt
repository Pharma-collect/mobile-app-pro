package fr.isen.emelian.pharma_collect_pro.ui.prescription.finishOrders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONObject
import java.math.BigDecimal

class FinishOrderFragment : Fragment() , View.OnClickListener {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    private lateinit var idOrder: IDs
    private lateinit var navController: NavController
    private lateinit var orderId: String
    private val myUser: User = User()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idOrder = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_container_order, container, false)
        setView(root)
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

    private fun setView(root: View) {
        val orderID: TextView = root.findViewById(R.id.id_order)
        val preparator : TextView = root.findViewById(R.id.preparator)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val locker: TextView = root.findViewById(R.id.locker_nb)

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order_detail/getOrderDetailsByOrder"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {

                    val jsonArray = jsonResponse.optJSONArray("result")
                    //val jsonArrayProduct = JSONObject(jsonArray["product"].toString())
                    val listProduct: MutableList<String> = ArrayList()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val product = JSONObject(item.get("product").toString())
                        val order = JSONObject(item.get("order").toString())

                        listProduct.add(product["title"].toString())
                        orderID.text = "ID : " + order["id"]
                        clientID.text = order["id_client"].toString()
                        detailText.text = order["detail"].toString()
                        statusOrder.text = "Order status : " + order["status"].toString()
                        preparator.text = "Preparator : " + order["id_preparator"].toString()
                        locker.text = "Locker ID : " + order["id_container"].toString()

                    }

                    val adapterPres: ArrayAdapter<String>? = context?.let { it1 -> ArrayAdapter(it1, android.R.layout.simple_list_item_1, listProduct) }
                    val listPres: ListView = view!!.findViewById(R.id.product_list)
                    listPres.adapter = adapterPres

                }else{

                    Toast.makeText(context, "Error while getting order info", Toast.LENGTH_LONG).show()

                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                    .show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    params["Authorization"] = myUser.token.toString()
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["order_id"] = orderId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

        orderId = idOrder.id.toString()
    }

}