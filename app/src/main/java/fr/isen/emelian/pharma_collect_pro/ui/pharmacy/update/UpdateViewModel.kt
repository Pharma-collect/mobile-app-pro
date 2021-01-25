package fr.isen.emelian.pharma_collect_pro.ui.pharmacy.update

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import org.json.JSONObject

class UpdateViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
        FileService()
    private val context = getApplication<Application>().applicationContext

    private val _pharmaName = MutableLiveData<String>()
    private val _pharmaPhone = MutableLiveData<String>()
    private val _pharmaRoad = MutableLiveData<String>()
    private val _pharmaRoadNumber = MutableLiveData<String>()
    private val _pharmaCity = MutableLiveData<String>()
    private val _pharmaPostcode = MutableLiveData<String>()

    val pharmaName: LiveData<String> = _pharmaName
    val pharmaPhone: LiveData<String> = _pharmaPhone
    val pharmaRoad: LiveData<String> = _pharmaRoad
    val pharmaRoadNumber: LiveData<String> = _pharmaRoadNumber
    val pharmaCity: LiveData<String> = _pharmaCity
    val pharmaPostcode: LiveData<String> = _pharmaPostcode

    init {
        //User information
        launch {
            getInfoToUpdate()
        }
    }

    fun getInfoToUpdate(){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    Log.d("PharmaInfo", response.toString())
                    if (jsonResponse["success"] == true) {
                        var data = JSONObject(jsonResponse.get("result").toString())
                        display(data)
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
                    params["pharmacy_id"] = fileService.getData(context).pharma_id.toString()
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun updatePharmacy(){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/updatePharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    Log.d("PharmaInfo", response.toString())
                    if (jsonResponse["success"] == true) {
                        var data = JSONObject(jsonResponse.get("result").toString())

                        Toast.makeText(context, "Pharmacy successfully updated", Toast.LENGTH_LONG).show()

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
                    params["pharmacy_id"] = fileService.getData(context).pharma_id.toString()
                    params["name"] = _pharmaName.value.toString()
                    params["road_nb"] = _pharmaRoadNumber.value.toString()
                    params["road"] = _pharmaRoad.value.toString()
                    params["phone"] = _pharmaPhone.value.toString()
                    params["post_code"] = _pharmaPostcode.value.toString()
                    params["city"] = _pharmaCity.value.toString()
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun display(data: JSONObject){
        _pharmaName.value = data["name"].toString()
        _pharmaCity.value = data["city"].toString()
        _pharmaRoad.value = data["road"].toString()
        _pharmaRoadNumber.value = data["road_nb"].toString()
        _pharmaPhone.value = "0" + data["phone"].toString()
        _pharmaPostcode.value = data["post_code"].toString()
    }

}