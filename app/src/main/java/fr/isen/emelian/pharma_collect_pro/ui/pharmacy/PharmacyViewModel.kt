package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.PharmacyRepository
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PharmacyViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var myPharma: Pharmacy = Pharmacy()
    private var myRepo: PharmacyRepository = PharmacyRepository()
    private var myUser: User = User()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    //private val pharmaRepository: PharmacyRepository = PharmacyRepository()
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    private val _name = MutableLiveData<String>()
    private val _city = MutableLiveData<String>()
    private val _postcode = MutableLiveData<String>()
    private val _road = MutableLiveData<String>()
    private val _roadnb = MutableLiveData<String>()
    private val _phone = MutableLiveData<String>()
    private val _id = MutableLiveData<String>()
    private val _shop = MutableLiveData<String>()
    private val _admin = MutableLiveData<String>()
    private val _user = MutableLiveData<String>()

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
            while(myUser.pharma_id == null){
                delay(5)
            }
            val pharmaData: LiveData<Pharmacy> = liveData{
                myRepo.getPharmacyInfo(myUser.pharma_id.toString(), context)
                emit(myRepo.myPharma)
            }
        }
        launch {

        }
    }


    fun getPharmaData(pharmaId: String, context: Context): Flow<Pharmacy> = flow{

    }

    /*
     * Get all user
     */
//    fun countUserOfPharmacy(id: String) {
//        val requestQueue = Volley.newRequestQueue(context)
//        val url = "$backUrl/user_pro/getUserProByPharmacy"
//        val stringRequest: StringRequest =
//                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
//                    override fun onResponse(response: String?) {
//                        var jsonResponse: JSONObject = JSONObject(response)
//                        if (jsonResponse["success"] == true) {
//                            implBossAdminAmount(jsonResponse)
//                        }else{
//                            Toast.makeText(context, jsonResponse.toString(), Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }, object : Response.ErrorListener {
//                    override fun onErrorResponse(error: VolleyError) {
//                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
//                    }
//                }) {
//                    override fun getHeaders(): Map<String, String> {
//                        val params: MutableMap<String, String> = HashMap()
//                        params["Host"] = "node"
//                        params["Authorization"] = fileService.getData(context).token.toString()
//                        return params
//                    }
//                    override fun getParams(): MutableMap<String, String>? {
//                        val params: MutableMap<String, String> = HashMap()
//                        params["pharmacy_id"] = id
//                        return params
//                    }
//                }
//        requestQueue.add(stringRequest)
//    }


    fun implInfo(){
        _name.value = myRepo.myPharma.name
        _city.value = myRepo.myPharma.city
        _postcode.value = myRepo.myPharma.post_code
        if( myPharma.has_shop.toString() == "0"){
            _shop.value = "No"
        } else {
            _shop.value = "Yes"
        }
        _road.value = myRepo.myPharma.road
        _roadnb.value = myRepo.myPharma.road_nb
        _phone.value = "0${myRepo.myPharma.phone}"
        _id.value = "ID : ${myRepo.myPharma.id}"
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