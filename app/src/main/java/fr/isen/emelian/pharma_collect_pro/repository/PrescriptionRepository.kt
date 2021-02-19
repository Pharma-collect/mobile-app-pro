package fr.isen.emelian.pharma_collect_pro.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class PrescriptionRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    /**
     * Update a prescription to container state
     */
    fun updatePresToContainer(orderId: String, status: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/prescription/updatePrescription"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == false) {
                    Log.d("Error", "Error when updating prescription to container, reason : $jsonResponse")
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
                    params["id_prescription"] = orderId
                    params["status"] = status
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }


    /**
     * Get informations about the order when passing from order id
     */
    fun getOrderInfo(idPharma: String, token: String, idPrescription: String, price: TextView , detail: TextView, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item["id_prescription"].toString() == idPrescription) {
                            price.text = "Total price : " + item["total_price"] + "€"
                            detail.text = "Order detail : " + item["detail"]
                        }
                    }
                }else{
                    Log.d("Error", "Error when getting order infos, reason : $jsonResponse")
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
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get order informations
     */
    fun getOrderInfos(idPharma: String, token: String, locker: TextView, detail: TextView, idPrescription: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item["id_prescription"].toString() == idPrescription) {
                            detail.text = "Order detail : " + item["detail"]
                            locker.text = "Locker id : " + item["id_container"]
                        }
                    }
                }else{
                    Log.d("Error", "Error when getting order infos, reason : $jsonResponse")
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
                    params["pharmacy_id"] = idPharma
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get order By id
     */
    fun getOrderByIdPres(myImage: ImageView, orderID: String, token: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    getPictureUrl(myImage, token, data["id_prescription"].toString(), context)
                }else{
                    Log.d("Error", "Error when getting order id of prescription, reason : $jsonResponse")
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
                    params["order_id"] = orderID
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Get the picture url
     */
    private fun getPictureUrl(myImage: ImageView, token: String, prescriptionId: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/prescription/getPrescriptionById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    val myUri: Uri = Uri.parse(data["image_url"].toString())
                    Glide.with(context).load(myUri).into(myImage)
                }else{
                    Log.d("Error", "Error when getting picture url, reason : $jsonResponse")
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
                    params["prescription_id"] = prescriptionId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}