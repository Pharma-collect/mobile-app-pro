package fr.isen.emelian.pharma_collect_pro.ui.locker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class LockerFragment : Fragment(), View.OnClickListener {

    private lateinit var lockerViewModel: LockerViewModel
    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User = User()
    private val lockerRepository: LockerRepository =
            LockerRepository()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        lockerViewModel =
                ViewModelProvider(this).get(LockerViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_locker, container, false)

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val circleMenu: CircleMenu = root.findViewById(R.id.circle_menu)

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    val listNumber : MutableList<String> = ArrayList()
                    val listStatus : MutableList<String> = ArrayList()
                    if(jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            listNumber.add(item["id"].toString())
                            listStatus.add(item["status"].toString())
                        }
                    }

                    circleMenu.setMainMenu(Color.parseColor("#6E6E6E"), R.drawable.locker_logo, R.drawable.ic_baseline_clear_all_24).openMenu()
                    for (i in 0 until listNumber.size) {
                        if(listStatus[i] == "0"){
                            circleMenu.addSubMenu(Color.parseColor("#00FF00"), R.drawable.locker_logo)
                        }else{
                            circleMenu.addSubMenu(Color.parseColor("#6E6E6E"), R.drawable.locker_logo)
                        }
                    }

                    circleMenu.setOnMenuSelectedListener { index ->
                        val amount = IDs(BigDecimal(listNumber[index]))
                        val bundle = bundleOf("container_id" to amount)
                        navController.navigate(R.id.action_navigation_locker_to_lockerDetailsFragment2, bundle)
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
        view.findViewById<Button>(R.id.add_button).setOnClickListener(this)
        view.findViewById<Button>(R.id.clear_all_button).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_button -> navController.navigate(R.id.action_navigation_locker_to_addLockerFragment)
            R.id.clear_all_button -> changeFragmentAfterAllDeletion()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeFragmentAfterAllDeletion(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_clear, null)
        val question: TextView = navView.findViewById(R.id.sure_label)
        question.text = "Delete all containers of the pharmacy?"
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_confirm.setOnClickListener {
            context?.let { lockerRepository.deleteAllContainer(myUser.pharma_id.toString(), it) }
            alertDialog.dismiss()
            navController.navigate(R.id.action_locker_nav_to_locker_nav)
        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }
}