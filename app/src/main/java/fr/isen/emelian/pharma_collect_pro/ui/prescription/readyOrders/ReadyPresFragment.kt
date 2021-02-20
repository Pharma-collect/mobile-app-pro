package fr.isen.emelian.pharma_collect_pro.ui.prescription.readyOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.PrescriptionRepository
import kotlinx.android.synthetic.main.dialog_picture.view.*
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class ReadyPresFragment : Fragment(), View.OnClickListener  {

    private lateinit var idOrders: IDs
    private lateinit var navController: NavController
    private lateinit var orderIds: String
    var myUser: User = User()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private var myRepo: PrescriptionRepository = PrescriptionRepository()
    private var myPictureUrl: String = ""
    private var available = 0


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idOrders = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_ready_pres, container, false)
        setView(root)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_client_info).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_locker).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.prescription_image_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_client_info -> switchToClientInfo()
            R.id.button_back -> activity?.onBackPressed()
            R.id.button_locker -> switchToLockerSelection()
            R.id.prescription_image_view -> switchToPicture()
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_readyPresFragment_to_detailClientFragment, bundle)
    }

    private fun switchToLockerSelection(){
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerByPharmacy"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")
                    if(jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            if(item["status"].toString() == "0"){
                                this.available += 1
                            }
                        }
                    }
                    if(this.available > 0) {
                        val id = IDs(BigDecimal(orderIds))
                        val bundle = bundleOf("order_id" to id)
                        navController.navigate(R.id.action_readyPresFragment_to_selectLockerFragment, bundle)
                    } else {
                        Toast.makeText(context, "No locker available for the moment", Toast.LENGTH_LONG)
                            .show()
                    }
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

    private fun switchToPicture(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_picture, null)
        val picture = navView.findViewById<ImageView>(R.id.pres_big)
        val myUri: Uri = Uri.parse(this.myPictureUrl)
        context?.let { Glide.with(it).load(myUri).into(picture) }
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_back.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setView(root: View) {
        orderIds = idOrders.id.toString()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.token = jsonObject.optString("token")
            myUser.pharma_id = jsonObject.optInt("pharmaId")
        }

        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val detailText: TextView = root.findViewById(R.id.detail_text)
        val preparator: TextView = root.findViewById(R.id.id_preparator)
        val urlImage: ImageView = root.findViewById(R.id.prescription_image_view)
        val price: TextView = root.findViewById(R.id.price_text)

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/prescription/getPrescriptionById"
        val stringRequest: StringRequest =
            @SuppressLint("SetTextI18n")
            object : StringRequest(Method.POST, url, Response.Listener<String> {
                val jsonResponse = JSONObject(it)
                Log.d("PharmaInfo", it.toString())
                if (jsonResponse["success"] == true) {
                    val data = JSONObject(jsonResponse.get("result").toString())
                    val myUri: Uri = Uri.parse(data["image_url"].toString())

                    orderID.text = "ID : " + data["id"].toString()
                    clientID.text = data["id_client"].toString()
                    statusOrder.text = "Current status : " + data["status"]
                    Glide.with(root.context).load(myUri).into(urlImage)
                    preparator.text = "Preparator ID : " + data["id_preparator"]

                    this.myPictureUrl = data["image_url"].toString()

                    context?.let { it1 ->
                        myRepo.getOrderInfo(myUser.pharma_id.toString(), myUser.token.toString(), orderIds, price, detailText,
                            it1
                        )
                    }
                }else{

                    Log.d("error", "Error while getting infos")

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
                    params["prescription_id"] = orderIds
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}