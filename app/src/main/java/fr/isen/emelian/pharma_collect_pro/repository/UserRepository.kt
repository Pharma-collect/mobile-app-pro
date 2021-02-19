package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.MainActivity
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class UserRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService = FileService()

    /**
     * Login to the pro app with pro credentials
     */
    fun logRequest(username: String, password: String, context: Context){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/loginPro"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == true) {
                        val data = JSONObject(jsonResponse.get("result").toString())
                        val pharma = JSONObject(data.get("pharmacy").toString())
                        fileService.saveData(data["id"].toString(), data["username"].toString(), pharma["id"].toString(),
                                pharma["name"].toString(), data["token"].toString(), context)

                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "Wrong username or password", Toast.LENGTH_LONG).show()
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(context, "Wrong email or password or internal error", Toast.LENGTH_LONG)
                            .show()
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
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}

