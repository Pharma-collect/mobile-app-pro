package fr.isen.emelian.pharma_collect_pro.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ProductRepository {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    /**
     * Get a product with its id
     */
    fun updateProductsofArray(productsArray: JSONArray, context: Context) {
        for(i in 0 until productsArray.length()) {
            val item = productsArray.getJSONObject(i)
            getProductToUpdate(item["id_product"].toString(), item["quantity"].toString(), context)
        }
    }

    /**
     * Find the product to update
     */
    private fun getProductToUpdate(productId: String, quantity: String, context: Context) {
        var myNewQuantity: Int
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/product/getProductById"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    myNewQuantity = data["capacity"].toString().toInt() - quantity.toInt()
                    updateProduct(myNewQuantity.toString(), productId, context)
                }else{
                    Log.d("Error", "Error when finding product to update, reason : $jsonResponse")
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
                    params["product_id"] = productId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Update the product
     */
    private fun updateProduct(myNewQuantity: String, productId: String, context: Context) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/product/updateProduct"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == false) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }else{
                    Log.d("Error", "Error when updating products, reason : $jsonResponse")
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
                    params["product_id"] = productId
                    params["capacity"] = myNewQuantity
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}
