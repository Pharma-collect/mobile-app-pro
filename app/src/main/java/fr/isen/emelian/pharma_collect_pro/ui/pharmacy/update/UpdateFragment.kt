package fr.isen.emelian.pharma_collect_pro.ui.pharmacy.update

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.User
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
        setView(root)
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

    /**
     * Function which update into the database the pharmacy information
     * It will take the modified fields
     */
    private fun update() {
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
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setCancelable(true)
                    val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)
                    builder.setView(navView)
                    val alertDialog = builder.create()
                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialog.show()

                    Handler().postDelayed({
                        alertDialog.dismiss()
                        navController.navigate(R.id.action_navigation_update_to_navigation_pharmacy)
                    }, 6000)

                }else{
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setCancelable(true)
                    val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_error, null)
                    builder.setView(navView)
                    val alertDialog = builder.create()
                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialog.show()

                    Handler().postDelayed({
                        alertDialog.dismiss()
                    }, 5000)
                    Log.d("Error", "Error when updating pharmacy, reason $jsonResponse")
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                        .show()
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

    private fun setView(root: View) {
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

        /**
         * Get current pharmacy information
         */
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/pharmacy/getPharmacyById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())

                    pharmaName.setText(data["name"].toString())
                    pharmaPhone.setText("0" + data["phone"].toString())
                    pharmaRoadNb.setText(data["road_nb"].toString())
                    pharmaRoad.setText(data["road"].toString())
                    pharmaCity.setText(data["city"].toString())
                    pharmaPostcode.setText(data["post_code"].toString())

                }else{
                    Log.d("ResponseJSON", jsonResponse.toString())
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG)
                    .show()
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
    }
}