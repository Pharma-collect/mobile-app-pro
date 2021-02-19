package fr.isen.emelian.pharma_collect_pro.ui.prescription.detailClient

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailClientFragment : Fragment(), View.OnClickListener {

    private lateinit var idClient: IDs
    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var clientId: String
    private val myUser: User = User()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idClient = arguments!!.getParcelable("client_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_detail_client, container, false)
        setView(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_back_client).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_back_client -> activity?.onBackPressed()
        }
    }

    private fun setView(root: View) {
        clientId = idClient.id.toString()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        /**
         * Get client information by id for the clien sheet
         */
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_client/getUserClientById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                Log.d("PharmaInfo", response.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    val username: TextView = root.findViewById(R.id.client_username)
                    val clientID: TextView = root.findViewById(R.id.client_id)
                    val firstname: TextView = root.findViewById(R.id.client_firstname)
                    val lastname: TextView = root.findViewById(R.id.client_lastname)
                    val dob: TextView = root.findViewById(R.id.client_dob)
                    val age: TextView = root.findViewById(R.id.client_age)
                    val phone: TextView = root.findViewById(R.id.client_phone)
                    val mail: TextView = root.findViewById(R.id.client_mail)

                    username.text = data["username"].toString()
                    clientID.text = "Client ID : " + data["id"].toString()
                    firstname.text = "Firstname : " + data["name"].toString()
                    lastname.text = "Lastname : " + data["lastname"].toString()
                    dob.text = "Date of birth : " + data["birth"].toString()
                    phone.text = "Phone : 0" + data["phone"].toString()
                    mail.text = "E-mail : " + data["mail"].toString()
                    age.text = "Age : " + calculAge(data["birth"].toString()).toString()

                }else{

                    Toast.makeText(context, "Error while getting order info", Toast.LENGTH_LONG).show()

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
                    params["user_id"] = clientId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    /**
     * Calcul the age with the date of birth
     */
    private fun calculAge(date: String): Int {

        var age = 0

        try {
            val dates = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(date)
            val today = Calendar.getInstance()
            val birth = Calendar.getInstance()

            birth.time = dates

            val thisYear = today.get(Calendar.YEAR)
            val yearBirth = birth.get(Calendar.YEAR)

            age = thisYear - yearBirth

            val thisMonth = today.get(Calendar.MONTH)
            val birthMonth = birth.get(Calendar.MONTH)

            if(birthMonth > thisMonth){
                age--
            }else if (birthMonth == thisMonth){
                val thisDay = today.get(Calendar.DAY_OF_MONTH)
                val birthDay = birth.get(Calendar.DAY_OF_MONTH)

                if(birthDay > thisDay){
                    age--
                }
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return age
    }
}