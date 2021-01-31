package fr.isen.emelian.pharma_collect_pro.ui.prescription

import android.app.Application
import android.util.Log
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
import org.json.JSONArray
import org.json.JSONObject

class PrescriptionViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    var myUser: User = User()

    private val context = getApplication<Application>().applicationContext
    private val fileService: FileService = FileService()

    private val _pending = MutableLiveData<String>().apply { value = "Amount : " }
    private val _ready = MutableLiveData<String>().apply { value = "Amount : " }
    private val _finish = MutableLiveData<String>().apply { value = "Amount : " }
    private val _container = MutableLiveData<String>().apply { value = "Amount : " }


    val pending: LiveData<String> = _pending
    val ready: LiveData<String> = _ready
    val finish: LiveData<String> = _finish
    val container: LiveData<String> = _container

    init {
        launch {
            myUser = fileService.getData(context)
            getOrderInfo(myUser.pharma_id.toString())
        }
    }

    /**
     * get all orders of a pharmacy
     */
    private fun getOrderInfo(idPharma: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == true) {
                        val jsonArray = jsonResponse.optJSONArray("result")
                        getOrders(jsonArray)
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
     * Get the amount of order for every state
     */
    fun getOrders(jsonArray: JSONArray){
        var pending = 0
        var finish = 0
        var ready = 0
        var container = 0
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item.optString("status").toString() == "pending") {
                pending++
            }
            if(item.optString("status").toString() == "ready") {
                ready++
            }
            if(item.optString("status").toString() == "finish") {
                finish++
            }
            if(item.optString("status").toString() == "container") {
                container++
            }
        }
        _pending.value = "Amount : $pending"
        _ready.value = "Amount : $ready"
        _finish.value = "Amount : $finish"
        _container.value = "Amount : $container"

    }

}