package fr.isen.emelian.pharma_collect_pro.ui.prescription.pendingOrders

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class DetailPrescriptionViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    var myUser: User = User()
    var idOrder: String = ""
    //var idPrescription: String = ""

    private val context = getApplication<Application>().applicationContext
    private val fileService: FileService = FileService()

    private val _orderID = MutableLiveData<String>().apply { value = "Order ID : " }
    private val _clientID = MutableLiveData<String>().apply { value = "Client ID  : " }
    private val _statusOrder = MutableLiveData<String>().apply { value = "Current state : " }
    //private val _detailText = MutableLiveData<String>().apply { value = "RAS" }
    private val _totalPrice = MutableLiveData<String>().apply { value = "Total price : " }

    val orderID: LiveData<String> = _orderID
    val clientID: LiveData<String> = _clientID
    val statusOrder: LiveData<String> = _statusOrder
    //val detailText: LiveData<String> = _detailText
    val totalPrice: LiveData<String> = _totalPrice

    init {
        launch {
            myUser = fileService.getData(context)
            getPrescriptionById(idOrder)
        }

        launch {
            //getPrescriptionURL(idPrescription)
        }
    }

    /**
     * Get the info of the prescription
     */
    private fun getPrescriptionById(idOrder: String){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())

                    _orderID.value = "ID : " + data["id"]
                    _clientID.value = data["id_client"].toString()
                    _statusOrder.value = "Current status : " + data["status"]
                    //_detailText.value = data["detail"].toString()
                    _totalPrice.value = "Total price : " + data["total_price"] + "€"

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
                    params["order_id"] = idOrder
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get The prescription info like its url for example
     */
    private fun getPrescriptionURL(idPrescription: String){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/prescription/getPrescriptionById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    //val data = JSONObject(jsonResponse.get("result").toString())

                        //displayPrescription(context, data["image_url"].toString())

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
                    params["order_id"] = idPrescription
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Function which display prescription image
     */
    fun displayPrescription(view: ImageView, imageUrl: String){
        //Glide.with(view.context).load(Uri.parse(imageUrl)).into(view)
    }

}