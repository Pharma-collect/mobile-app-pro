package fr.isen.emelian.pharma_collect_pro.ui.prescription.pendingOrders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
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
import com.bumptech.glide.Glide
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.PrescriptionRepository
import kotlinx.android.synthetic.main.dialog_picture.view.*
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal

class DetailPrescriptionFragment : Fragment(), View.OnClickListener {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var idOrder: IDs
    private lateinit var navController: NavController
    private lateinit var orderId: String
    private val myUser: User = User()
    private var myRepo: PrescriptionRepository = PrescriptionRepository()
    private var myPictureUrl: String = ""

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idOrder = arguments!!.getParcelable("order_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_detail_prescription, container, false)
        setView(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_client_info).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_back).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_treat).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.prescription_image_view).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_client_info -> switchToClientInfo()
            R.id.button_back -> activity?.onBackPressed()
            R.id.button_treat -> switchToSelectionInfo()
            R.id.prescription_image_view -> switchToPicture()
        }
    }

    private fun switchToClientInfo() {
        val client = view?.findViewById<TextView>(R.id.id_client)?.text.toString()
        val id = IDs(BigDecimal(client))
        val bundle = bundleOf("client_id" to id)
        navController.navigate(R.id.action_detailPrescriptionFragment_to_detailClientFragment, bundle)
    }

    private fun switchToSelectionInfo() {
        val myId = view?.findViewById<TextView>(R.id.id_order)?.text.toString().substring(5,8)
        val id = IDs(BigDecimal(myId))
        val bundle = bundleOf("order_id" to id)
        navController.navigate(R.id.action_detailPrescriptionFragment_to_selectProductFragment, bundle)
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
        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.token = jsonObject.optString("token")
            myUser.pharma_id = jsonObject.optInt("pharmaId")
        }

        orderId = idOrder.id.toString()

        val orderID: TextView = root.findViewById(R.id.id_order)
        val clientID: TextView = root.findViewById(R.id.id_client)
        val statusOrder: TextView = root.findViewById(R.id.status_order)
        val urlImage: ImageView = root.findViewById(R.id.prescription_image_view)
        val price: TextView = root.findViewById(R.id.price_text)
        val button: Button = root.findViewById(R.id.button_treat)

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
                    clientID.text = data["id_client"].toString()
                    statusOrder.text = "Current status : " + data["status"]
                    Glide.with(root.context).load(myUri).into(urlImage)
                    this.myPictureUrl = data["image_url"].toString()

                    context?.let { it1 ->
                        myRepo.getOrderInfo(myUser.pharma_id.toString(), myUser.token.toString(), orderId, orderID, price,
                            it1
                        )
                    }

                    button.visibility = View.VISIBLE

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
                    params["prescription_id"] = orderId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }
}