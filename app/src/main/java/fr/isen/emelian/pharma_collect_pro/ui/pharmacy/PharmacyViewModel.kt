package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

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
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class PharmacyViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var myPharma: Pharmacy = Pharmacy()
    private var myUser: User = User()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    //private val pharmaRepository: PharmacyRepository = PharmacyRepository()
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    private val _name = MutableLiveData<String>().apply { value = "Pharmacy name" }
    private val _city = MutableLiveData<String>().apply { value = "City" }
    private val _postcode = MutableLiveData<String>().apply { value = "Postcode" }
    private val _road = MutableLiveData<String>().apply { value = "Road" }
    private val _roadnb = MutableLiveData<String>().apply { value = "Road number" }
    private val _phone = MutableLiveData<String>().apply { value = "Phone" }
    private val _id = MutableLiveData<String>().apply { value = "ID" }
    private val _shop = MutableLiveData<String>().apply { value = "Yes/No" }
    private val _admin = MutableLiveData<String>().apply { value = "5" }
    private val _user = MutableLiveData<String>().apply { value = "1" }

    val name: LiveData<String> = _name
    val city: LiveData<String> = _city
    val postcode: LiveData<String> = _postcode
    val road_name: LiveData<String> = _road
    val road_nb: LiveData<String> = _roadnb
    val phone: LiveData<String> = _phone
    val id: LiveData<String> = _id
    val shop: LiveData<String> = _shop
    val admin: LiveData<String> = _admin
    val user: LiveData<String> = _user

    init {
        launch{
            myUser = fileService.getData(context)
            implPharma(myUser.pharma_id.toString())
        }
        launch{
            myUser = fileService.getData(context)
            countUserOfPharmacy(myUser.pharma_id.toString())
        }
    }

    /*
     * Get pharmacy by id
     */
    fun implPharma(id: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                            var jsonResponse: JSONObject = JSONObject(response)
                            if (jsonResponse["success"] == true) {
                                var jsonArray = jsonResponse.optJSONArray("result")
                                var data = JSONObject(jsonResponse.get("result").toString())
                                implInterface(data)
                                implInfo()
                        }else{
                                Toast.makeText(context, jsonResponse.toString(), Toast.LENGTH_LONG).show()
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
                        params["pharmacy_id"] = id
                        return params
                    }
                }
        requestQueue.add(stringRequest)
    }

    /*
     * Get all user
     */
    fun countUserOfPharmacy(id: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/getUserProByPharmacy"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {
                            implBossAdminAmount(jsonResponse)
                        }else{
                            Toast.makeText(context, jsonResponse.toString(), Toast.LENGTH_LONG).show()
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
                        params["pharmacy_id"] = id
                        return params
                    }
                }
        requestQueue.add(stringRequest)
    }


    fun implInterface(data: JSONObject){
        myPharma.name = data["name"].toString()
        myPharma.has_shop = data["has_shop"].toString()
        myPharma.boss = data["boss"].toString()
        myPharma.phone = data["phone"].toString()
        myPharma.road = data["road"].toString()
        myPharma.road_nb = data["road_nb"].toString()
        myPharma.city = data["city"].toString()
        myPharma.post_code = data["post_code"].toString()
        myPharma.has_shop = data["has_shop"].toString()
        myPharma.id = data["id"].toString()
    }

    fun implInfo(){
        _name.value = myPharma.name
        _city.value = myPharma.city
        _postcode.value = myPharma.post_code
        if( myPharma.has_shop.toString() == "0"){
            _shop.value = "No"
        } else {
            _shop.value = "Yes"
        }
        _road.value = myPharma.road
        _roadnb.value = myPharma.road_nb
        _phone.value = "0${myPharma.phone}"
        _id.value = "ID : ${myPharma.id}"
    }

    fun implBossAdminAmount(jsonResponse: JSONObject){
        var user = -1
        var admin = 0
        var jsonArray = jsonResponse.optJSONArray("result")
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item["is_admin"] != "1"){
                user++
            } else {
                admin++
            }
        }
        _admin.value = user.toString()
        _user.value = admin.toString()
    }
}