package fr.isen.emelian.pharma_collect_pro.ui.prescription.readyOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
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
import com.hitomi.cmlibrary.CircleMenu
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.LockerRepository
import fr.isen.emelian.pharma_collect_pro.repository.OrderRepository
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*
import org.json.JSONObject
import java.io.File

class SelectLockerFragment : Fragment(), View.OnClickListener {

    private lateinit var idOrders: IDs
    private lateinit var navController: NavController
    private lateinit var orderIds: String
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User = User()
    private val orderRepository: OrderRepository = OrderRepository()
    private val lockerRepository: LockerRepository = LockerRepository()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idOrders = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_select_locker, container, false)

        orderIds = idOrders.id.toString()

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
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    val listNumber : MutableList<String> = ArrayList()
                    val listId:  MutableList<String> = ArrayList()
                    val listState:  MutableList<String> = ArrayList()
                    if(jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            listNumber.add(item["container_number"].toString())
                            listId.add(item["id"].toString())
                            listState.add(item["status"].toString())
                        }
                    }

                    circleMenu.setMainMenu(Color.parseColor("#6E6E6E"), R.drawable.locker_logo, R.drawable.ic_baseline_clear_all_24).openMenu()
                    for (i in 0 until listId.size) {
                            circleMenu.addSubMenu(Color.parseColor("#00FF00"), R.drawable.locker_logo)
                    }

                    circleMenu.setOnMenuSelectedListener { index ->

                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        builder.setCancelable(true)
                        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_locker, null)
                        val textViewId: TextView = navView.findViewById(R.id.container_selected_id)
                        val textViewNumber: TextView = navView.findViewById(R.id.container_selected_nb)
                        val textViewState: TextView = navView.findViewById(R.id.container_selected_state)

                        textViewId.text = "Container ID : " + listId[index]
                        textViewNumber.text = "Container number : " + listNumber[index]
                        if(listState[index] == "0"){
                            textViewState.text = "Container state : Available"
                        } else {
                            textViewState.text = "Container state : already used"
                        }


                        builder.setView(navView)
                        val alertDialog = builder.create()
                        alertDialog.show()

                        navView.button_confirm.setOnClickListener {
                            Toast.makeText(context, "You clicked " + listId[index] + " confirm", Toast.LENGTH_LONG).show()
                            context?.let { it1 -> orderRepository.updateOrderToContainer(orderIds, listId[index], "container", it1) }
                            context?.let { it1 -> lockerRepository.updateContainer("1", listId[index], it1) }
                            alertDialog.dismiss()
                            navController.navigate((R.id.action_selectLockerFragment_to_navigation_prescription))
                        }
                        navView.button_cancel.setOnClickListener {
                            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
                            alertDialog.dismiss()
                        }
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