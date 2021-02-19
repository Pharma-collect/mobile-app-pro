package fr.isen.emelian.pharma_collect_pro.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class OrderRepository {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"

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
                        Log.d("Error", "Error when updating order, reason : $jsonResponse")
                    } else {
                        Toast.makeText(context, "Successfully changed", Toast.LENGTH_LONG).show()
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
                        Log.d("Error", "Error when updating order to container, reason : $jsonResponse")
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
                        params["order_id"] = orderId
                        params["status"] = status
                        params["id_container"] = idContainer
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
                    Log.d("Error", "Error when finding order to update to container, reason : $jsonResponse")
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
                    params["pharmacy_id"] = idPharmacy
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get the client Id with order id
     */
    fun getClientId(orderId: String, idPharmacy: String, token: String, totalPrice: String, products: JSONArray, preparator: String, detail: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    createOrder(data["id_client"].toString(),idPharmacy, totalPrice, products, data["id_prescription"].toString(), preparator, detail, context)
                    deleteOrderById(orderId, context)
                }else{
                    Log.d("Error", "Error when getting client info, reason : $jsonResponse")
                }
            }, Response.ErrorListener { error ->
                Log.d("Error", error.toString())
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    params["Authorization"] = token
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["order_id"] = orderId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Create a new order when taking in charge an order
     */
    private fun createOrder(idClient: String,
                    idPharmacy: String,
                    totalPrice: String,
                    products: JSONArray,
                    idPrescription: String,
                    idPreparator: String,
                    detail: String,
                    context: Context) {

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/createOrder"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {

                    val data = JSONObject(jsonResponse.get("result").toString())
                    updateOrderToReady(data["id"].toString(), "ready", idPreparator, detail, context)

                }else{
                    Log.d("Error", "Error when creating order, reason : $jsonResponse")
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
                    params["id_client"] = idClient
                    params["id_pharmacy"] = idPharmacy
                    params["total_price"] = totalPrice
                    params["products"] = products.toString()
                    params["id_prescription"] = idPrescription
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Delete an order with its id
     */
    private fun deleteOrderById(orderId: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/deleteOrderById"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
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
                    params["order_id"] = orderId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}