package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject

class LockerRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
        FileService()
    private var myUser: User = User()


    /**
     * Add a new container to the pharmacy
     */
    fun addContainer(amount: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/addXContainerToPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    Toast.makeText(context, "$amount container has been created", Toast.LENGTH_LONG).show()
                }else{
                    Log.d("Error", "Error when adding containers, reason : $jsonResponse")
                }
            }, Response.ErrorListener { error ->
                Log.d("Error", error.toString())
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

    /**
     * Update the state of the container --> switch between fill and empty
     */
    fun updateContainer(
        status: String,
        containerId: String,
        context: Context
    ) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/updateContainer"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    Toast.makeText(context, "Container successfully updated", Toast.LENGTH_LONG).show()
                }else{
                    Log.d("Error", "Error when updating containers, reason : $jsonResponse")
                }
            }, Response.ErrorListener { error ->
                Log.d("Error", error.toString())
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

    /**
     * Delete a container of the pharmacy with its id
     */
    fun deleteContainer(containerId: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/deleteContainerById"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == true) {
                        Toast.makeText(context, "Container successfully deleted", Toast.LENGTH_LONG).show()
                    }else{
                        Log.d("Error", "Error when deleting containers, reason : $jsonResponse")
                    }
                }, Response.ErrorListener { error ->
                    Log.d("Error", error.toString())
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

    /**
     * Delete all the containers of the pharmacy
     */
    fun deleteAllContainer(pharmaId: String, context: Context) {
        myUser = fileService.getData(context)
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/deleteAllContainersFromPharma"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == true) {
                        Toast.makeText(context, "All container successfully deleted", Toast.LENGTH_LONG).show()
                    }else{
                        Log.d("Error", "Error when deleting all containers, reason : $jsonResponse")
                    }
                }, Response.ErrorListener { error ->
                    Log.d("Error", error.toString())
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

