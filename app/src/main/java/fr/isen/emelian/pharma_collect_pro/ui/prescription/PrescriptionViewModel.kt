package fr.isen.emelian.pharma_collect_pro.ui.prescription

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class PrescriptionViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    var myUser: User = User()

    private val context = getApplication<Application>().applicationContext
    private val fileService: FileService = FileService()

    private val _currentUser = MutableLiveData<String>().apply { value = "Current user : " }
    private val _idUser = MutableLiveData<String>().apply { value = "ID  : " }
    private val _orderCurrent = MutableLiveData<String>().apply { value = "Order in charge" }
    private val _pending = MutableLiveData<String>().apply { value = "Amount : " }
    private val _ready = MutableLiveData<String>().apply { value = "Amount : " }
    private val _finish = MutableLiveData<String>().apply { value = "Amount : " }
    private val _container = MutableLiveData<String>().apply { value = "Amount : " }


    val currentUser: LiveData<String> = _currentUser
    val idUser: LiveData<String> = _idUser
    val orderCurrent: LiveData<String> = _orderCurrent
    val pending: LiveData<String> = _pending
    val ready: LiveData<String> = _ready
    val finish: LiveData<String> = _finish
    val container: LiveData<String> = _container

    init {
        //User information
        launch {
            myUser = fileService.getData(context)
            _currentUser.value = "User : ${myUser.username}"
            _idUser.value = "ID : ${myUser.id}"
        }

        //Orders information
        launch {
            myUser = fileService.getData(context)
            getOrderInfo(myUser.id.toString(), myUser.pharma_id.toString())
        }
    }

    /*
     get All orders of a pharmacy
     */
    fun getOrderInfo(idUser: String, idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
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
        var pending = 0
        var finish = 0
        var ready = 0
        var container = 0
        var assignee = 0
        var all = 0
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item.optString("status").toString() == "pending") {
                pending++
                all++
            }
            if(item.optString("status").toString() == "ready") {
                ready++
                all++
            }
            if(item.optString("status").toString() == "finish") {
                finish++
                all++
            }
            if(item.optString("status").toString() == "container") {
                container++
                all++
            }
            if(item.optString("status").toString() == id) {
                assignee++
            }
        }
        _pending.value = "Amount : $pending"
        _ready.value = "Amount : $ready"
        _finish.value = "Amount : $finish"
        _container.value = "Amount : $container"

        if(assignee > 0){
            _orderCurrent.value = "You have took in charge $assignee order(s). Click to see"
        } else {
            _orderCurrent.value = "You do not have any order in progress"
        }
    }

}