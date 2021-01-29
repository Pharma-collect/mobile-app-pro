package fr.isen.emelian.pharma_collect_pro.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HomeViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext
    var myUser: User = User()

    // Declaration of variables which contains the values
    private val _current = MutableLiveData<String>().apply { value = "Current user : " }
    private val _id = MutableLiveData<String>().apply { value = "ID  : " }
    private val _order = MutableLiveData<String>().apply { value = "Order in charge" }
    private val _prescription = MutableLiveData<String>().apply { value = "Waiting : " }
    private val _locker = MutableLiveData<String>().apply { value = "Available : " }
    private val _name = MutableLiveData<String>().apply { value = "Pharmacy name" }

    // Declaration of livedata which communicate with fragment
    val current: LiveData<String> = _current
    val id: LiveData<String> = _id
    val order: LiveData<String> = _order
    val prescription: LiveData<String> = _prescription
    val locker: LiveData<String> = _locker
    val name: LiveData<String> = _name

    // Initialisation of coroutines
    init {
        //User information thread
        launch{
            myUser = fileService.getData(context)
            _current.value = "User : ${myUser.username}"
            _id.value = "ID : ${myUser.id}"
        }

        //Pharmacy information thread
        launch {
            myUser = fileService.getData(context)
            _name.value = myUser.pharma_name.toString()
        }

        //Orders information thread
        launch {
            myUser = fileService.getData(context)
            getOrderInfo(myUser.id.toString(), myUser.pharma_id.toString())
        }

        //Locker information thread
        launch {
            myUser = fileService.getData(context)
            getLockerInfo(myUser.pharma_id.toString())
        }
    }


    /**
     * Get all orders for a pharmacy
     */
    private fun getOrderInfo(idUser: String, idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    getOrders(jsonArray, idUser)
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
                    params["Authorization"] = fileService.getData(context).token.toString()
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get all available lockers of the pharmacy
     */
    private fun getLockerInfo(idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == false ) {
                    _locker.value = "No locker registered"
                }else{
                    val jsonArray = jsonResponse.optJSONArray("result")
                    getAmountAvailableLocker(jsonArray)
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                        .show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    params["Authorization"] = fileService.getData(context).token.toString()
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Function use to know if the current user has an order in charge
     */
    private fun getOrders(jsonArray: JSONArray, id: String){
        var waiting = 0
        var assignee = 0
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item.optString("status").toString() == "pending") {
                waiting++
            }
            if(item.optString("id_preparator").toString() == id) {
                assignee++
            }
        }
        _prescription.value = "To do : $waiting"
        if(assignee > 0){
            _order.value = "$assignee order(s) in charge"
        } else {
            _order.value = "No order assigned"
        }
    }

    /**
     * Get the amount of available lockers for the pharmacy
     */
    fun getAmountAvailableLocker(jsonArray: JSONArray){
        var available = 0
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item.optString("status").toString() == "0") {
                available++
            }
        }
        if(available > 0){
            _locker.value = "Available : $available"
        } else {
            _locker.value = "No locker available"
        }
    }
}
