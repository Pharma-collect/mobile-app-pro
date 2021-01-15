package fr.isen.emelian.pharma_collect_pro.ui.stats.graphs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.services.FileService
import org.json.JSONObject


class OrderGraphFragment : Fragment() {

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService = FileService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_graph, container, false)

        val pieChart: PieChart = view.findViewById(R.id.order_pie_chart)
        val order = ArrayList<PieEntry>()

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getAllOrders" // For the moment while correct get order by pharmacy
        val stringRequest: StringRequest =
            //object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> { // For the moment while correct get order by pharmacy
            object : StringRequest(Request.Method.GET, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        var jsonArray = jsonResponse.optJSONArray("result")

                        var pending = 0.0F
                        var ready = 0.0F
                        var container = 0.0F
                        var finish = 0.0F

                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            if(item.optString("status").toString() == "pending") {
                                pending++
                            }
                            if(item.optString("status").toString() == "ready") {
                                ready++
                            }
                            if(item.optString("status").toString() == "container") {
                                container++
                            }
                            if(item.optString("status").toString() == "finish") {
                                finish++
                            }
                        }
                        var amount_pending = pending
                        var amount_ready = ready
                        var amount_container = container
                        var amount_finish = finish

                        order.add(PieEntry(amount_pending, "Pending"))
                        order.add(PieEntry(amount_ready, "Ready"))
                        order.add(PieEntry(amount_container, "Container"))
                        order.add(PieEntry(amount_finish, "Finish"))

                        val pieDataSet = PieDataSet(order, "Order")
                        pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                        pieDataSet.setDrawIcons(false)
                        pieDataSet.sliceSpace = 3f
                        pieDataSet.iconsOffset = MPPointF(0F, 40F)
                        pieDataSet.selectionShift = 5f

                        val data = PieData(pieDataSet)
                        data.setValueTextSize(11f)
                        data.setValueTextColor(Color.WHITE)
                        pieChart.data = data
                        pieChart.highlightValues(null)
                        pieChart.invalidate()
                        pieChart.animateXY(2500, 2500)

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
                    return params
                }
            }
        requestQueue.add(stringRequest)
        return view
    }
}