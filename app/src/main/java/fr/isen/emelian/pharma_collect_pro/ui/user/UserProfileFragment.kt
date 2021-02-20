package fr.isen.emelian.pharma_collect_pro.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class UserProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private var myUser: User = User()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_profile, container, false)
        setView(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_user_info).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_user_info -> activity?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setView(root: View) {
        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.id = jsonObject.optInt("id")
            myUser.token = jsonObject.optString("token")
            myUser.pharma_name = jsonObject.optString("pharmaName")
            myUser.username = jsonObject.optString("username")
        }

        val username: TextView = root.findViewById(R.id.info_username)
        val userId: TextView = root.findViewById(R.id.info_id)
        val pharmaName: TextView = root.findViewById(R.id.info_pharma)
        val pharmaId: TextView = root.findViewById(R.id.info_pharma_id)

        username.text = myUser.username.toString()
        userId.text = "ID : " + myUser.id.toString()
        pharmaName.text = myUser.pharma_name.toString()
        pharmaId.text = "ID : " + myUser.pharma_id.toString()

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")

                    val listPrescription: MutableList<String> = ArrayList()
                    val listType: MutableList<String> = ArrayList()
                    val listState: MutableList<String> = ArrayList()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item["id_preparator"].toString() == myUser.id.toString()){
                            if(item["id_prescription"].toString() == "null") {
                                listPrescription.add(item["id"].toString())
                            } else {
                                listPrescription.add(item["id_prescription"].toString())
                            }
                            listType.add(item["id_prescription"].toString())
                            listState.add(item["status"].toString())
                        }
                    }

                    val adapterPres: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listPrescription) }
                    val listPres: ListView = root.findViewById(R.id.info_list)
                    listPres.adapter = adapterPres
                    listPres.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->

                        if(listType[p2] == "null" && listState[p2] == "pending"){
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_detailOrderFragment)
                        } else if(listType[p2] != "null" && listState[p2] == "pending"){
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_detailPrescriptionFragment)
                        } else if(listType[p2] == "null" && listState[p2] == "container"){
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_containerOrderFragment)
                        } else if(listType[p2] != "null" && listState[p2] == "container") {
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_containerPresFragment)
                        } else if(listType[p2] == "null" && listState[p2] == "finish") {
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_finishOrderFragment)
                        } else if(listType[p2] != "null" && listState[p2] == "finish") {
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_finishPresFragment)
                        } else if(listType[p2] == "null" && listState[p2] == "ready") {
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_readyOrderFragment)
                        } else if(listType[p2] != "null" && listState[p2] == "ready") {
                            bundle(listPrescription[p2], R.id.action_userProfileFragment_to_readyPresFragment)
                        }
                    }

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
                    params["Authorization"] =  myUser.token.toString()
                    return params
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] =  myUser.pharma_id.toString()
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    private fun bundle(id_received: String, action: Int) {
        val id = IDs(BigDecimal(id_received))
        val bundle = bundleOf("order_id" to id)
        navController.navigate(action, bundle)
    }
}