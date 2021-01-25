package fr.isen.emelian.pharma_collect_pro.ui.locker.actions

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.LockerRepository
import org.json.JSONObject
import java.io.File

class LockerDetailsFragment : Fragment(), View.OnClickListener {

    private var selected: String = "0"
    private lateinit var navController: NavController
    lateinit var id_container: IDs
    private val lockerRepository: LockerRepository =
            LockerRepository()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User =
            User()
    private lateinit var amount: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id_container = arguments!!.getParcelable("container_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_locker_details, container, false)

        var pickerState: NumberPicker = root.findViewById(R.id.picker)
        amount = id_container!!.id.toString()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerById"
        val stringRequest: StringRequest =
                object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(response: String?) {
                        var jsonResponse: JSONObject = JSONObject(response)
                        Log.d("PharmaInfo", response.toString())
                        if (jsonResponse["success"] == true) {
                            var data = JSONObject(jsonResponse.get("result").toString())
                            val containerNumber: TextView = root.findViewById(R.id.locker_number)
                            val containerId: TextView = root.findViewById(R.id.locker_id)
                            val containerState: TextView = root.findViewById(R.id.locker_state)

                            containerNumber.text = "Locker number : " + data["container_number"].toString()
                            containerId.text = "ID : " + data["id"].toString()

                            if(data["status"].toString() == "0") {
                                containerState.text = "Current status : Empty"
                            } else {
                                containerState.text = "Current status : Fill"
                            }


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
                        params["container_id"] = amount
                        return params
                    }
                }
        requestQueue.add(stringRequest)


        pickerState.maxValue = 1
        pickerState.minValue = 0

        pickerState.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                selected = p2.toString()
            }
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.cancel_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.update_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.delete_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cancel_btn -> activity?.onBackPressed()
            R.id.update_btn -> context?.let { lockerRepository.updateContainer(selected, amount, it) }
            R.id.delete_btn -> context?.let { lockerRepository.deleteContainer(amount, it) }
        }
    }



}

