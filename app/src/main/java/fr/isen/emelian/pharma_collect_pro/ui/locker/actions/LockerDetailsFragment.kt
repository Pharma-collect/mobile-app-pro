package fr.isen.emelian.pharma_collect_pro.ui.locker.actions

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
import android.widget.NumberPicker
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
import fr.isen.emelian.pharma_collect_pro.repository.LockerRepository
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*
import org.json.JSONObject
import java.io.File

class LockerDetailsFragment : Fragment(), View.OnClickListener {

    private var selected: String = "0"
    private lateinit var navController: NavController
    private lateinit var idContainer: IDs
    private val lockerRepository: LockerRepository = LockerRepository()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val myUser: User = User()
    private lateinit var amount: String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idContainer = arguments!!.getParcelable("container_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_locker_details, container, false)
        setView(root)
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
            R.id.update_btn -> changeFragmentAfterUpdate()
            R.id.delete_btn -> lockerVerifDeletion()
        }
    }

    /**
     * Fragment switch back after the deletion of a locker
     */
    @SuppressLint("SetTextI18n")
    private fun changeFragmentAfterDeletion(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_clear, null)
        val question: TextView = navView.findViewById(R.id.sure_label)
        question.text = "Do you really want to delete this container ?"
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        navView.button_confirm.setOnClickListener {
            context?.let { lockerRepository.deleteContainer(amount, it) }
            alertDialog.dismiss()

            val builderTwo: AlertDialog.Builder = AlertDialog.Builder(context)
            builderTwo.setCancelable(true)
            val navViewTwo: View = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)
            builderTwo.setView(navViewTwo)
            val alertDialogTwo = builderTwo.create()
            alertDialogTwo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialogTwo.show()

            Handler().postDelayed({
                alertDialogTwo.dismiss()
                activity?.onBackPressed()
            }, 5000)

        }
        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }

    /**
     * Fragment switch back after the update of a locker
     */
    @SuppressLint("SetTextI18n")
    private fun changeFragmentAfterUpdate(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_clear, null)
        val question: TextView = navView.findViewById(R.id.sure_label)
        question.text = "Update this container ?"
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        navView.button_confirm.setOnClickListener {
            context?.let { lockerRepository.updateContainer(selected, amount, it) }
            alertDialog.dismiss()

            val builderTwo: AlertDialog.Builder = AlertDialog.Builder(context)
            builderTwo.setCancelable(true)
            val navViewTwo: View = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)
            builderTwo.setView(navViewTwo)
            val alertDialogTwo = builderTwo.create()
            alertDialogTwo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialogTwo.show()

            Handler().postDelayed({
                alertDialogTwo.dismiss()
                activity?.onBackPressed()
            }, 5000)
        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun lockerVerifDeletion() {
        val containerState = view?.findViewById<TextView>(R.id.locker_state)?.text.toString()
        if(containerState == "Current status : Fill"){
            Toast.makeText(context, "You can't delete a non available container", Toast.LENGTH_LONG).show()
        } else {
            changeFragmentAfterDeletion()
        }
    }

    private fun setView(root: View) {
        val pickerState: NumberPicker = root.findViewById(R.id.picker)
        amount = idContainer.id.toString()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        /**
         * Get container info with its id
         */
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
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
                    params["container_id"] = amount
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

        pickerState.maxValue = 1
        pickerState.minValue = 0

        pickerState.setOnValueChangedListener { _, _, p2 -> selected = p2.toString() }
    }
}

