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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
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
    private lateinit var id_user: MutableList<String>
    private lateinit var type_user: String

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
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    val jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {

                        val list_admin: MutableList<String> = ArrayList()
                        val list_user: MutableList<String> = ArrayList()

                        var user = 0F
                        var admin = 0F
                        val jsonArray = jsonResponse.optJSONArray("result")
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            if (item["is_admin"] == 1) {
                                admin++
                                list_admin.add(item["username"].toString())
                            } else {
                                user++
                                list_user.add(item["username"].toString())
                            }
                        }

                        val amount_admin = admin
                        val amount_user = user

                        users.add(BarEntry(1F, amount_user))
                        users.add(BarEntry(2F, amount_admin))

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
                                val row: String = users.get(x).x.toString()
                                val type: String = users.get(x).y.toString()

                                if(row == 1F.toString()) {
                                    id_user = list_user
                                    type_user = "User"
                                } else if(row == 2F.toString()) {
                                    id_user = list_admin
                                    type_user = "Admin"
                                }

                                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                                builder.setCancelable(true)
                                val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_order, null)
                                val textView_type: TextView = navView.findViewById(R.id.type_order)

                                val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, id_user) }
                                val list_id: ListView = navView.findViewById(R.id.list_view)
                                list_id.adapter = adapter

                                textView_type.setText(type_user)

                                builder.setView(navView)
                                val alertDialog = builder.create()
                                alertDialog.show()
                            }
                        })

                    } else {
                        Toast.makeText(context, jsonResponse.toString(), Toast.LENGTH_LONG).show()
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