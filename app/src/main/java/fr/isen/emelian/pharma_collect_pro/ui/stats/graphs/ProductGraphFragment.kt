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


class ProductGraphFragment : Fragment(), View.OnClickListener {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var navController: NavController
    private var myUser: User = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_graph, container, false)

        val barChart: BarChart = view.findViewById(R.id.products_barchart)
        val products = ArrayList<BarEntry>()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/product/getProductsByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    val jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        var title: String
                        var capacity: String
                        var row : Float
                        val jsonArray = jsonResponse.optJSONArray("result")
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)

                            row = i.toFloat()
                            if(item["capacity"].toString() == "null") {
                                capacity = "0"
                            }
                            else {
                                capacity = item["capacity"].toString()
                            }
                            title = item["title"].toString()

                            products.add(BarEntry(row, capacity.toFloat(), title))
                        }


                        val barDataSet = BarDataSet(products, "Products")
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
                                val type: String? = products.get(x).y.toString()
                                val capa: String? = products.get(x).data.toString()

                                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                                builder.setCancelable(true)
                                val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_product, null)
                                val textView_type: TextView = navView.findViewById(R.id.name_product)
                                val textView_capacity: TextView = navView.findViewById(R.id.capacity_product)

                                textView_type.setText(capa)
                                textView_capacity.setText(type)

                                builder.setView(navView)
                                var alertDialog = builder.create()
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
        view.findViewById<Button>(R.id.back_product_graph).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_product_graph -> activity?.onBackPressed()
        }
    }

}