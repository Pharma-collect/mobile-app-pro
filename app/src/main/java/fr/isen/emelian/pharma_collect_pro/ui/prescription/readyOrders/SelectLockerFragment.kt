package fr.isen.emelian.pharma_collect_pro.ui.prescription.readyOrders

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.hitomi.cmlibrary.CircleMenu
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.LockerRepository
import fr.isen.emelian.pharma_collect_pro.ui.locker.LockerViewModel
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class SelectLockerFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User = User()
    private val lockerRepository: LockerRepository =
        LockerRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_select_locker, container, false)

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val circleMenu: CircleMenu = root.findViewById(R.id.circle_menu)

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getEmptyContainerByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    val listNumber : MutableList<String> = ArrayList()
                    if(jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            listNumber.add(item["id"].toString())
                        }
                    }

                    circleMenu.setMainMenu(Color.parseColor("#6E6E6E"), R.drawable.locker_logo, R.drawable.ic_baseline_clear_all_24).openMenu()
                    for (i in 0 until listNumber.size) {
                            circleMenu.addSubMenu(Color.parseColor("#00FF00"), R.drawable.locker_logo)
                    }

                    circleMenu.setOnMenuSelectedListener { index ->

                        Toast.makeText(context, "You clicked " + listNumber[index], Toast.LENGTH_LONG).show()
                        // Make dialog confirmation function

                    }

                } else if (jsonResponse["success"] == false && jsonResponse["error"] == "Il n'existe pas de containers"){
                    Toast.makeText(context, "No container registered", Toast.LENGTH_LONG).show()
                } else{
                    Log.d("ResponseJSON", jsonResponse.toString())
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_back -> activity?.onBackPressed()
        }
    }

}