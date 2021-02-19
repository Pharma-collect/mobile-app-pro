package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class PharmacyRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService = FileService()
    var numUsersAdmins: MutableMap<String, String> = HashMap()

    /**
     * Get pharmacy information
     */
    fun getPharmacyInfo(pharmaId: String, context: Context) : Pharmacy{
        val requestQueue = Volley.newRequestQueue(context)
        val myPharma = Pharmacy()
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    Log.d("PharmaInfo", it.toString())
                    if (jsonResponse["success"] == true) {
                        val data = JSONObject(jsonResponse.get("result").toString())
                        myPharma.id = data["id"].toString()
                        myPharma.name = data["name"].toString()
                        myPharma.has_shop = data["has_shop"].toString()
                        myPharma.boss = data["boss"].toString()
                        myPharma.phone = data["phone"].toString()
                        myPharma.road = data["road"].toString()
                        myPharma.road_nb = data["road_nb"].toString()
                        myPharma.city = data["city"].toString()
                        myPharma.post_code = data["post_code"].toString()
                        myPharma.success = jsonResponse["success"].toString()
                    }else{
                        Log.d("Error", "Error when getting pharma info, reason : $jsonResponse")
                    }
                }, Response.ErrorListener { error ->
                    Log.d("Error", error.toString())
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Host"] = "node"
                        params["Authorization"] = fileService.getData(context).token.toString()
                        return params
                    }
                    override fun getParams(): MutableMap<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["pharmacy_id"] = pharmaId
                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
        return myPharma
    }

    /**
     * Get all user of the pharmacy
     */
    fun countUserOfPharmacy(id: String, context: Context){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/getUserProByPharmacy"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == true) {
                        numUsersAdmins = implBossAdminAmount(jsonResponse)
                        Log.d("PharmaInfo", numUsersAdmins.toString())
                    }else{
                        Log.d("Error", "Error when counting users, reason : $jsonResponse")
                    }
                }, Response.ErrorListener { error ->
                    Log.d("Error", error.toString())
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
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Function which get the amount of admin and user
     */
    fun implBossAdminAmount(jsonResponse: JSONObject): MutableMap<String, String>{
        val numbers: MutableMap<String, String> = HashMap()
        var users = 0
        var admins = 0
        val jsonArray = jsonResponse.optJSONArray("result")

        for (i in 0 until jsonArray!!.length()) {
            val item = jsonArray.getJSONObject(i)
            if(item["is_admin"] != 1){
                users++
            } else {
                admins++
            }
        }
        numbers["users"] = users.toString()
        numbers["admins"]  = admins.toString()
        return numbers
    }

}
