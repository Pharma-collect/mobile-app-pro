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
import java.io.File
import java.math.BigDecimal

class FinishFragment : Fragment(), View.OnClickListener {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var navController: NavController
    var myUser: User = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_finish, container, false)
        setView(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_button).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back_button -> activity?.onBackPressed()
        }
    }

    private fun bundlePrescription(id: String, action: Int) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    val idPres = IDs(BigDecimal(data["id_prescription"].toString()))
                    val bundle = bundleOf("order_id" to idPres)
                    navController.navigate(action, bundle)
                }else{
                    Log.d("error", "Error while getting infos")
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
                    params["order_id"] = id
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    private fun setView(root: View) {
        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.token = jsonObject.optString("token")
            myUser.pharma_id = jsonObject.optInt("pharmaId")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")

                    val listPrescription: MutableList<String> = ArrayList()
                    val listPrescriptionId: MutableList<String> = ArrayList()
                    val listOrders: MutableList<String> = ArrayList()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item["id_prescription"].toString() != "null" && item["status"].toString() == "finish") {
                            listPrescription.add(item["id"].toString())
                            listPrescriptionId.add(item["id_prescription"].toString())
                        }
                        if(item["id_prescription"].toString() == "null" && item["status"].toString() == "finish") {
                            listOrders.add(item["id"].toString())
                        }
                    }

                    val adapterPres: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listPrescriptionId) }
                    val listPres: ListView = root.findViewById(R.id.prescription_list_view)
                    listPres.adapter = adapterPres
                    listPres.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                        bundlePrescription(listPrescription[p2], R.id.action_finishFragment_to_finishPrescriptionFragment)
                    }

                    val adapterOrder: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listOrders) }
                    val listOrder: ListView = root.findViewById(R.id.order_list_view)
                    listOrder.adapter = adapterOrder
                    listOrder.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                        val id = IDs(BigDecimal(listOrders[p2]))
                        val bundle = bundleOf("order_id" to id)
                        navController.navigate(R.id.action_finishFragment_to_finishOrderFragment, bundle)
                    }

                }else{
                    Log.d("ResponseJSON", jsonResponse.toString())
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                    .show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    params["Authorization"] =  myUser.token.toString()
                    return params
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] =  myUser.pharma_id.toString()
                    return params
                }
            }

        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}