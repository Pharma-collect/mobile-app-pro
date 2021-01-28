package fr.isen.emelian.pharma_collect_pro.ui.stats.graphs

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONObject
import java.io.File

class UserProGraphFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var myUser: User = User()
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var idUser: MutableList<String>
    private lateinit var typeUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_pro_graph, container, false)

        val barChart: BarChart = view.findViewById(R.id.users_pro_barchart)
        val users = ArrayList<BarEntry>()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/user_pro/getUserProByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {

                    val listAdmin: MutableList<String> = ArrayList()
                    val listUser: MutableList<String> = ArrayList()

                    var user = 0F
                    var admin = 0F
                    val jsonArray = jsonResponse.optJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if (item["is_admin"] == 1) {
                            admin++
                            listAdmin.add(item["username"].toString())
                        } else {
                            user++
                            listUser.add(item["username"].toString())
                        }
                    }

                    val amountAdmin = admin
                    val amountUser = user

                    users.add(BarEntry(1F, amountUser))
                    users.add(BarEntry(2F, amountAdmin))

                    val barDataSet = BarDataSet(users, "Users")
                    barDataSet.setColors(*ColorTemplate.PASTEL_COLORS)
                    barDataSet.setDrawIcons(false)
                    barDataSet.iconsOffset = MPPointF(0F, 40F)

                    val data = BarData(barDataSet)
                    data.setValueTextSize(11f)
                    data.setValueTextColor(Color.WHITE)
                    barChart.data = data
                    barChart.highlightValues(null)
                    barChart.invalidate()
                    barChart.xAxis.isEnabled = false
                    barChart.axisLeft.isEnabled = false
                    barChart.axisRight.isEnabled = false
                    barChart.animateXY(1000, 1000)

                    barChart.setOnChartValueSelectedListener(object:
                            OnChartValueSelectedListener {
                        override fun onNothingSelected() {
                            // Code
                        }

                        override fun onValueSelected(e: Entry, h: Highlight){
                            val x = barChart.data.getDataSetForEntry(e).getEntryIndex(e as BarEntry?)
                            val row: String = users[x].x.toString()

                            if(row == 1F.toString()) {
                                idUser = listUser
                                typeUser = "User"
                            } else if(row == 2F.toString()) {
                                idUser = listAdmin
                                typeUser = "Admin"
                            }

                            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                            builder.setCancelable(true)
                            val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_order, null)
                            val textViewType: TextView = navView.findViewById(R.id.type_order)

                            val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, idUser) }
                            val listId: ListView = navView.findViewById(R.id.list_view)
                            listId.adapter = adapter

                            textViewType.text = typeUser

                            builder.setView(navView)
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                    })

                } else {
                    Toast.makeText(context, jsonResponse.toString(), Toast.LENGTH_LONG).show()
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_user_graph).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_user_graph -> activity?.onBackPressed()
        }
    }
}