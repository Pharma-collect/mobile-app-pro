package fr.isen.emelian.pharma_collect_pro.ui.stats.graphs

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import fr.isen.emelian.pharma_collect_pro.R
import org.json.JSONObject
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.request.StringRequest
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import java.io.File

class OrderGraphFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    lateinit var idOrder: MutableList<String>
    private val myUser: User =
        User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_graph, container, false)

        val pieChart: PieChart = view.findViewById(R.id.order_pie_chart)
        val order = ArrayList<PieEntry>()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        /**
         * Function which get all orders of a pharmacy
         * Make a Pie chart with orders and these states
         */
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/order/getOrderByPharmacy" // For the moment while correct get order by pharmacy
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")

                    var pending = 0.0F
                    var ready = 0.0F
                    var container = 0.0F
                    var finish = 0.0F

                    val listPending : MutableList<String> = ArrayList()
                    val listFinish : MutableList<String> = ArrayList()
                    val listReady : MutableList<String> = ArrayList()
                    val listContainer : MutableList<String> = ArrayList()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item.optString("status").toString() == "pending") {
                            pending++
                            listPending.add("Order ID : " + item["id"].toString())
                        }
                        if(item.optString("status").toString() == "ready") {
                            ready++
                            listReady.add("Order ID : " + item["id"].toString())
                        }
                        if(item.optString("status").toString() == "container") {
                            container++
                            listContainer.add("Order ID : " + item["id"].toString())
                        }
                        if(item.optString("status").toString() == "finish") {
                            finish++
                            listFinish.add("Order ID : " + item["id"].toString())
                        }
                    }

                    val amountPending = pending
                    val amountReady = ready
                    val amountContainer = container
                    val amountFinish = finish

                    order.add(PieEntry(amountPending, "Pending"))
                    order.add(PieEntry(amountReady, "Ready"))
                    order.add(PieEntry(amountContainer, "Container"))
                    order.add(PieEntry(amountFinish, "Finish"))

                    val pieDataSet = PieDataSet(order, "Order")
                    pieDataSet.setColors(*ColorTemplate.PASTEL_COLORS)
                    pieDataSet.setDrawIcons(false)
                    pieDataSet.sliceSpace = 3f
                    pieDataSet.iconsOffset = MPPointF(0F, 40F)
                    pieDataSet.selectionShift = 5f

                    val data = PieData(pieDataSet)
                    data.setValueTextSize(11f)
                    data.setValueTextColor(Color.WHITE)
                    pieChart.data = data
                    pieChart.highlightValues(null)
                    pieChart.setTransparentCircleColor(0)
                    pieChart.invalidate()
                    pieChart.animateXY(1000, 1000)

                    pieChart.setOnChartValueSelectedListener(object:
                            OnChartValueSelectedListener {
                        override fun onNothingSelected() {
                            // Code
                        }

                        override fun onValueSelected(e: Entry, h: Highlight){
                            val x = pieChart.data.getDataSetForEntry(e).getEntryIndex(e as PieEntry?)
                            val type: String = order[x].label

                            when (type) {
                                "Pending" -> {
                                    idOrder = listPending
                                }
                                "Ready" -> {
                                    idOrder = listReady
                                }
                                "Finish" -> {
                                    idOrder = listFinish
                                }
                                "Container" -> {
                                    idOrder = listContainer
                                }
                            }

                            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                            builder.setCancelable(true)
                            val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_order, null)
                            val textViewType: TextView = navView.findViewById(R.id.type_order)

                            val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, idOrder) }
                            val listId: ListView = navView.findViewById(R.id.list_view)
                            listId.adapter = adapter

                            textViewType.text = type

                            builder.setView(navView)
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                    })

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
        view.findViewById<Button>(R.id.back_order_graph).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_order_graph -> activity?.onBackPressed()
        }
    }
}