package fr.isen.emelian.pharma_collect_pro.ui.pharmacy.update

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.CollapsingToolbarLayout
import fr.isen.emelian.pharma_collect_pro.MainActivity
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import fr.isen.emelian.pharma_collect_pro.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.File

class UpdateFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var myUser: User = User()
    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_update_pharmacy, container, false)

        val pharmaName: EditText = root.findViewById(R.id.pharma_name_update)
        val pharmaPhone: EditText = root.findViewById(R.id.pharma_phone_update)
        val pharmaRoadNb: EditText = root.findViewById(R.id.pharma_road_nb_update)
        val pharmaRoad: EditText = root.findViewById(R.id.pharma_road_update)
        val pharmaCity: EditText = root.findViewById(R.id.pharma_city_update)
        val pharmaPostcode: EditText = root.findViewById(R.id.pharma_postcode_update)

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    Log.d("PharmaInfo", response.toString())
                    if (jsonResponse["success"] == true) {
                        var data = JSONObject(jsonResponse.get("result").toString())

                        pharmaName.setText(data["name"].toString())
                        pharmaPhone.setText("0" + data["phone"].toString())
                        pharmaRoadNb.setText(data["road_nb"].toString())
                        pharmaRoad.setText(data["road"].toString())
                        pharmaCity.setText(data["city"].toString())
                        pharmaPostcode.setText(data["post_code"].toString())

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
                    params["Authorization"] = myUser.token.toString()
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = myUser.pharma_id.toString()
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_pharma_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.update_pharma_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_pharma_btn -> activity?.onBackPressed()
            R.id.update_pharma_btn -> update()
        }
    }

    fun update() {
        val phone = view?.findViewById<EditText>(R.id.pharma_phone_update)?.text
        val name = view?.findViewById<EditText>(R.id.pharma_name_update)?.text
        val road = view?.findViewById<EditText>(R.id.pharma_road_update)?.text
        val roadnb = view?.findViewById<EditText>(R.id.pharma_road_nb_update)?.text
        val city = view?.findViewById<EditText>(R.id.pharma_city_update)?.text
        val postcode = view?.findViewById<EditText>(R.id.pharma_postcode_update)?.text

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/updatePharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        Toast.makeText(context, "Pharmacy successfully updated", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, MainActivity::class.java)
                        context?.startActivity(intent)
                    }else{
                        Toast.makeText(context, "Failed to update pharmacy", Toast.LENGTH_LONG).show()
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
                    params["Authorization"] = myUser.token.toString()
                    return params
                }
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] = myUser.pharma_id.toString()
                    params["name"] = name.toString()
                    params["road"] = road.toString()
                    params["road_nb"] = roadnb.toString()
                    params["phone"] = phone.toString()
                    params["post_code"] = postcode.toString()
                    params["city"] = city.toString()
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}