package fr.isen.emelian.pharma_collect_pro.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONException
import org.json.JSONObject

class MyRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    /*
     * Get user pro by id
     */
    fun getUserInformations(
        context: Context,
        userData: User){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/getUserProById"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
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
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    params["Authorization"] = userData.token.toString() //Is working
                    return params
                }
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = java.util.HashMap()
                    params["user_id"] = userData.id.toString() //Need to pass into integer
                    return params
                }
            }
        requestQueue.add(stringRequest)
    }

}