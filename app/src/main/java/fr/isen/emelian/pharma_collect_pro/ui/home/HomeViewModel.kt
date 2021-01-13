package fr.isen.emelian.pharma_collect_pro.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.UserRepository
import fr.isen.emelian.pharma_collect_pro.repository.PharmacyRepository
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONArray
import org.json.JSONObject


class HomeViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    var myUser: User = User()

    private val _current = MutableLiveData<String>().apply { value = "Current user : " }
    private val _id = MutableLiveData<String>().apply { value = "ID  : " }
    private val _order = MutableLiveData<String>().apply { value = "Order in charge" }
    private val _prescription = MutableLiveData<String>().apply { value = "Waiting : " }
    private val _locker = MutableLiveData<String>().apply { value = "Available : " }
    private val _name = MutableLiveData<String>().apply { value = "Pharmacy name" }

    val current: LiveData<String> = _current
    val id: LiveData<String> = _id
    val order: LiveData<String> = _order
    val prescription: LiveData<String> = _prescription
    val locker: LiveData<String> = _locker
    val name: LiveData<String> = _name

    //private val homeRepository: UserRepository = UserRepository()
    private val pharmaRepository: PharmacyRepository = PharmacyRepository()
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    init {
        //User information
        launch{
            myUser = fileService.getData(context)
            _current.value = "User : ${myUser.username}"
            _id.value = "ID : ${myUser.id}"
        }

        //Pharmacy information
        launch {
            myUser = fileService.getData(context)
            _name.value = myUser.pharma_name.toString()
        }

        //Orders information
        launch {
            myUser = fileService.getData(context)
            getOrderInfo(myUser.id.toString(), myUser.pharma_id.toString())
        }

        //Locker information
        launch {
            myUser = fileService.getData(context)
            getLockerInfo(myUser.pharma_id.toString())
        }
    }

    /*
     * Order functions
     */

    /*
     get All orders of a pharmacy
     */
    fun getOrderInfo(idUser: String, idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        //val url = "$backUrl/order/getOrderByPharmacy"
        val url = "$backUrl/order/getAllOrders" // For the moment while correct get order by pharmacy
        val stringRequest: StringRequest =
            //object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> { // For the moment while correct get order by pharmacy
            object : StringRequest(Request.Method.GET, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        var jsonArray = jsonResponse.optJSONArray("result")
                        getOrders(jsonArray, idUser)
                    }else{
                        Log.d("ResponseJSON", jsonResponse.toString())
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
                    params["Authorization"] = fileService.getData(context).token.toString()
                    return params
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.add(stringRequest)
    }

    fun getOrders(jsonArray: JSONArray, id: String){
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
            _order.value = "You have took in charge $assignee order(s). Click to see"
        } else {
            _order.value = "You do not have any order in progress"
        }
    }

    /*
     * Locker functions
     */

    fun getLockerInfo(idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        //val url = "$backUrl/container/getContainerByPharmacy"
        val url = "$backUrl/container/getAllContainers" // For the moment while correct get container by pharmacy
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == false ) {
                            _locker.value = "No locker registered"
                    }else{
                        var jsonArray = jsonResponse.optJSONArray("result")
                        getAmountAvailableLocker(jsonArray)
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
                    params["Authorization"] = fileService.getData(context).token.toString()
                    return params
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.add(stringRequest)
    }

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
