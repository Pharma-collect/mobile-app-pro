package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class LockerRepository {

    private lateinit var navController: NavController
    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
        FileService()
    private var myUser: User = User()

    fun addContainer(amount: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/addXContainerToPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {

                        Toast.makeText(context, amount + " container has been created", Toast.LENGTH_LONG).show()

                    }else{
                        Toast.makeText(context, "Failed to create containers", Toast.LENGTH_LONG).show()
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
                    params["pharmacy_id"] = myUser.pharma_id.toString()
                    params["nb_of_containers"] = amount

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun updateContainer(
        status: String,
        containerId: String,
        context: Context
    ) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/updateContainer"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {

                        Toast.makeText(context, "Container successfully updated", Toast.LENGTH_LONG).show()
                        

                    }else{
                        Toast.makeText(context, "Failed to udate container", Toast.LENGTH_LONG).show()
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
                    params["container_id"] = containerId
                    params["status"] = status

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun deleteContainer(containerId: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/deleteContainerById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {
                            Toast.makeText(context, "Container successfully deleted", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, "Error while deleting container", Toast.LENGTH_LONG).show()
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
                        params["container_id"] = containerId

                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun deleteAllContainer(pharmaId: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/deleteAllContainersFromPharma"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        if (jsonResponse["success"] == true) {

                            Toast.makeText(context, "All container successfully deleted", Toast.LENGTH_LONG).show()

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
                        params["pharmacy_id"] = pharmaId

                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}

