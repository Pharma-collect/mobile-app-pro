package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.MainActivity
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class UserRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
        FileService()

    /*
     * Login pro
     */
    fun logRequest(username: String, password: String, context: Context){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/loginPro"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {
                            var data = JSONObject(jsonResponse.get("result").toString())
                            var pharma = JSONObject(data.get("pharmacy").toString())
                            fileService.saveData(data["id"].toString(), data["username"].toString(), pharma["id"].toString(),
                                pharma["name"].toString(), data["token"].toString(), context)
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }else{
                            Toast.makeText(context, "Wrong username or password", Toast.LENGTH_LONG).show()
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                                .show()
                    }
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Host"] = "node"
                        return params
                    }

                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["username"] = username
                        params["password"] = password

                        return params
                    }
                }
        requestQueue.add(stringRequest)
    }
}

