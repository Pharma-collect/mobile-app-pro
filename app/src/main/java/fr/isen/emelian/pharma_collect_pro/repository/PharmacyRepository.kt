package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class PharmacyRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
            FileService()
    var myPharma: Pharmacy = Pharmacy()

    /*
     * Get pharmacy information
     */
    fun getPharmacyInfo(pharmaId: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {
                            Log.d("ResponseJSON", jsonResponse.toString())
                            var jsonArray = jsonResponse.optJSONArray("result")
                            for (i in 0 until jsonResponse.length()) {
                                myPharma.name = jsonResponse.optString("name").toString()
                                myPharma.has_shop = jsonResponse.optString("has_shop").toString()
                                myPharma.boss = jsonResponse.optString("boss").toString()
                                myPharma.phone = jsonResponse.optString("phone").toString()
                                myPharma.road = jsonResponse.optString("road").toString()
                                myPharma.road_nb = jsonResponse.optString("road_nb").toString()
                                myPharma.city = jsonResponse.optString("city").toString()
                                myPharma.post_code = jsonResponse.optString("post_code").toString()
                                Log.d("TESTTEST", myPharma.toString())
                            }
                            /*var data = JSONObject(jsonResponse.get("result").toString())
                                myPharma.name = data["name"].toString()
                                myPharma.has_shop = data["has_shop"].toString()
                                myPharma.boss = data["boss"].toString()
                                myPharma.phone = data["phone"].toString()
                                myPharma.road = data["road"].toString()
                                myPharma.road_nb = data["road_nb"].toString()
                                myPharma.city = data["city"].toString()
                                myPharma.post_code = data["post_code"].toString()*/
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
                        params["pharmacy_id"] = pharmaId
                        return params
                    }
                }
        requestQueue.add(stringRequest)
    }

    /*
     * Get user pro by id, admin verification
     */
    fun getUserInformations(id: String, context: Context){
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.cache.clear()
        val url = "$backUrl/user_pro/getUserProById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String> {
                    override fun onResponse(response: String) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {
                            Log.d("RequestSuccess", response)
                            Toast.makeText(context, "Success betUserProById", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Log.d("RequestError", response)
                            Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Log.d("RequestError", error.toString())
                    }
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> = HashMap()
                        headers["Host"] = "node"
                        headers["Authorization"] = fileService.getData(context).token.toString()
                        return headers
                    }
                    // Do not enter here
                    override fun getParams(): MutableMap<String, String>? {
                        val params: MutableMap<String, String> = java.util.HashMap()
                        params["user_id"] = id
                        return params
                    }
                }
        requestQueue.add(stringRequest)
    }
}
