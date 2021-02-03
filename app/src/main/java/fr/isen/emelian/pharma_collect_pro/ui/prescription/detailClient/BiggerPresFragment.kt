package fr.isen.emelian.pharma_collect_pro.ui.prescription.detailClient

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import org.json.JSONObject
import java.math.BigDecimal

class BiggerPresFragment : Fragment(), View.OnClickListener {

    private lateinit var myId: IDs
    private lateinit var navController: NavController
    private lateinit var id: String
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myId = arguments!!.getParcelable("url")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_bigger_pres, container, false)
        id = myId.id.toString()


        val urlImage: ImageView = root.findViewById(R.id.pres_big)

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
                        Glide.with(root.context).load(myUri).into(urlImage)

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
                        return params
                    }
                    override fun getParams(): MutableMap<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["prescription_id"] = id
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