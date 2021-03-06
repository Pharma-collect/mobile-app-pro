package fr.isen.emelian.pharma_collect_pro.ui.stats.graphs

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONObject
import java.io.File

class LockerGraphFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private var myUser: User = User()
    lateinit var nbContainer: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_locker_graph, container, false)
        setView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_locker_graph).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_locker_graph -> activity?.onBackPressed()
        }
    }

    private fun setView(view: View) {
        val pieChart: PieChart = view.findViewById(R.id.locker_pie_chart)
        val locker = ArrayList<PieEntry>()

        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.pharma_id = jsonObject.optInt("pharmaId")
            myUser.token = jsonObject.optString("token")
        }

        /**
         * Get all countainers for a pharmacy
         * Create a pie chart in function of the states
         */
        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/container/getContainerByPharmacy" // For the moment while correct get order by pharmacy
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")

                    var empty = 0
                    var fill = 0

                    val listEmpty : MutableList<String> = ArrayList()
                    val listFill : MutableList<String> = ArrayList()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        if(item["status"] == 0) {
                            empty++
                            listEmpty.add("Container number : " + item["container_number"].toString())
                        }
                        if(item["status"] == 1) {
                            fill++
                            listFill.add("Container number : " + item["container_number"].toString())
                        }
                    }
                    val amountEmpty = empty.toFloat()
                    val amountFill = fill.toFloat()

                    locker.add(PieEntry(amountEmpty, "Available"))
                    locker.add(PieEntry(amountFill, "Fill"))


                    val pieDataSet = PieDataSet(locker, "locker")
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
                    pieChart.invalidate()
                    pieChart.animateXY(1000, 1000)

                    pieChart.setOnChartValueSelectedListener(object:
                        OnChartValueSelectedListener {
                        override fun onNothingSelected() {
                            // Code
                        }

                        override fun onValueSelected(e: Entry, h: Highlight){
                            val x = pieChart.data.getDataSetForEntry(e).getEntryIndex(e as PieEntry?)
                            val type: String = locker[x].label

                            if(type == "Available") {
                                nbContainer = listEmpty
                            } else if(type == "Fill") {
                                nbContainer = listFill
                            }

                            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                            builder.setCancelable(true)
                            val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_order, null)
                            val textViewType: TextView = navView.findViewById(R.id.type_order)

                            val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, nbContainer) }
                            val listId: ListView = navView.findViewById(R.id.list_view)
                            listId.adapter = adapter

                            textViewType.text = type

                            builder.setView(navView)
                            val alertDialog = builder.create()
                            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
    }
}