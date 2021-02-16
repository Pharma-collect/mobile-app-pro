package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class OrderRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    /**
     * Update pending orders to ready
     */
    fun updateOrderToReady(orderId: String, status: String, preparatorId: String, detail: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/updateOrder"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == false) {
                        Toast.makeText(context, "Failed to change order state", Toast.LENGTH_LONG).show()
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                            .show()
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Host"] = "node"
                        return params
                    }
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["order_id"] = orderId
                        params["status"] = status
                        params["id_preparator"] = preparatorId
                        params["detail"] = detail
                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }


    /**
     * Update ready orders to container state
     */
    fun updateOrderToContainer(orderId: String, idContainer: String, status: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/updateOrder"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> {
                    val jsonResponse = JSONObject(it)
                    if (jsonResponse["success"] == false) {
                        Toast.makeText(context, "Failed to change order state", Toast.LENGTH_LONG).show()
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                            .show()
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Host"] = "node"
                        return params
                    }
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["order_id"] = orderId
                        params["status"] = status
                        //params["container_id"] = idContainer
                        return params
                    }
                }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }


    /**
     * Find an order to update to ready with its prescription id
     */
    fun findOrderToUpdate(idPharmacy: String, idPrescription: String, status: String, preparatorId: String, detail: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if (item["id_prescription"].toString() == idPrescription){
                            updateOrderToReady(item["id"].toString(), status, preparatorId, detail, context)
                        }
                    }
                }else{
                    Toast.makeText(context, "Failed to select containers", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                    .show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    return params
                }
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharmacy
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Find an order to update to container with its prescription id
     */
    fun findOrderToUpdateContainer(idPharmacy: String, idPrescription: String, status: String, idContainer:String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if (item["id_prescription"].toString() == idPrescription){
                            updateOrderToContainer(item["id"].toString(), idContainer, status, context)
                        }
                    }
                }else{
                    Toast.makeText(context, "Failed to select containers", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                    .show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    return params
                }
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = idPharmacy
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    //Make function to create order
}