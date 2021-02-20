package fr.isen.emelian.pharma_collect_pro.ui.products

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.diegodobelo.expandingview.ExpandingList
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import kotlinx.android.synthetic.main.fragment_product_selection.progressBar
import org.json.JSONObject
import java.io.File

class ProductListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var mExpandingList: ExpandingList? = null
    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private var myUser: User = User()
    private var selectedPrice = 0
    private var selectedCapacity = ">=0"
    private var minOrMax = "both"
    private var presNeeded = "both"

    private var spinnerList = arrayOf("All products",
        "In stock",
        "Out of stock",
        "By price (min)",
        "By price (max)",
        "Without Prescription",
        "With prescription")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product_list, container, false)

        mExpandingList = view.findViewById(R.id.list_product)

        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, spinnerList) }
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        view.findViewById<Button>(R.id.button_search)
            .setOnClickListener {
                mExpandingList!!.removeAllViews()
                setView(view, view.findViewById<EditText>(R.id.et_search).text.toString())
            }

        setView(view, "")

        return view
    }

    private fun setView(view: View, text: String) {
        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.token = jsonObject.optString("token")
            myUser.pharma_id = jsonObject.optInt("pharmaId")
        }

        val pickerState: NumberPicker = view.findViewById(R.id.nb_picker)
        pickerState.maxValue = 100
        pickerState.minValue = 0
        pickerState.setOnValueChangedListener { _, _, p2 -> selectedPrice = p2 }

        (view.findViewById(R.id.progressBar) as ProgressBar).visibility = View.VISIBLE

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/product/getProductsByPharmacy"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse["success"] == true) {
                    val jsonArray = jsonResponse.optJSONArray("result")

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var capacity: Int
                        if(item["capacity"].toString() == "null"){
                            capacity = 0
                        } else {
                            capacity = item["capacity"].toString().toInt()
                        }
                        if(item["title"].toString().contains(text, ignoreCase = true)){
                            if(selectedCapacity == ">=0" && selectedPrice == 0 && minOrMax == "both" && presNeeded == "both") {
                                createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                    item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                    item["prescription_only"].toString())
                            } else if (selectedCapacity == ">0" && selectedPrice == 0 && minOrMax == "both" && presNeeded == "both") {
                                if(capacity > 0) {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            } else if (selectedCapacity == "=0" && selectedPrice == 0 && minOrMax == "both" && presNeeded == "both") {
                                if(capacity == 0) {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            } else if (selectedCapacity == ">=0" && selectedPrice != 0 && minOrMax == "min" && presNeeded == "both") {
                                if(item["price"].toString().toFloat() <= selectedPrice.toFloat()) {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            } else if (selectedCapacity == ">=0" && selectedPrice != 0 && minOrMax == "max" && presNeeded == "both") {
                                if(item["price"].toString().toFloat() >= selectedPrice.toFloat()) {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            } else if (selectedCapacity == ">=0" && selectedPrice == 0 && minOrMax == "both" && presNeeded == "yes") {
                                if(item["prescription_only"].toString() == "0") {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            } else if (selectedCapacity == ">=0" && selectedPrice == 0 && minOrMax == "both" && presNeeded == "no") {
                                if(item["prescription_only"].toString() == "1") {
                                    createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                        item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                        item["prescription_only"].toString())
                                }
                            }
                        }
                    }

                    progressBar.visibility = View.INVISIBLE

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
                    params["Authorization"] =  myUser.token.toString()
                    return params
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["pharmacy_id"] =  myUser.pharma_id.toString()
                    return params
                }
            }

        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    private fun createItems(title: String, url: String, id: String, description: String, price: String, quantity: String, presNeeded: String) {

        val capacity: String = if(quantity == "null"){
            "0"
        } else {
            quantity
        }
        val prescriptionNeeded: String = if(presNeeded == "0"){
            "No"
        } else {
            "Yes"
        }

        addItem(
            title,
            arrayOf(
                "Id : $id",
                "Description : $description",
                "Price : $price €",
                "Quantity : $capacity",
                "Only with prescription : $prescriptionNeeded"
            ),
            R.color.design_default_color_background,
            url
        )
    }

    @SuppressLint("SetTextI18n")
    private fun addItem(title: String, subItems: Array<String>, colorRes: Int, url: String) {
        val item = mExpandingList!!.createNewItem(R.layout.expanding_layout_shop)
        val myUri: Uri = Uri.parse(url)

        if (item != null) {
            item.setIndicatorColorRes(colorRes)
            (item.findViewById(R.id.title) as TextView).text = title

            context?.let { Glide.with(it).load(myUri).into((item.findViewById(R.id.image) as ImageView)) }

            item.createSubItems(subItems.size)
            for (i in 0 until item.subItemsCount) {
                val view = item.getSubItemView(i)
                configureSubItem(view, subItems[i])
            }
        }
    }

    private fun configureSubItem(view: View, subTitle: String) {
        (view.findViewById(R.id.sub_title) as TextView).text = subTitle
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {
        //Nothing
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d("AAAAAA", p2.toString())
        when (p2) {
            0 -> {
                selectedPrice = 0
                selectedCapacity = ">=0"
                minOrMax = "both"
                presNeeded = "both"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.INVISIBLE
            }
            1 -> {
                selectedPrice = 0
                selectedCapacity = ">0"
                minOrMax = "both"
                presNeeded = "both"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.INVISIBLE
            }
            2 -> {
                selectedPrice = 0
                selectedCapacity = "=0"
                minOrMax = "both"
                presNeeded = "both"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.INVISIBLE
            }
            3 -> {
                selectedPrice = 0
                selectedCapacity = ">=0"
                minOrMax = "min"
                presNeeded = "both"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.VISIBLE
            }
            4 -> {
                selectedPrice = 0
                selectedCapacity = ">=0"
                minOrMax = "max"
                presNeeded = "both"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.VISIBLE
            }
            5 -> {
                selectedPrice = 0
                selectedCapacity = ">=0"
                minOrMax = "both"
                presNeeded = "yes"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.INVISIBLE
            }
            6 -> {
                selectedPrice = 0
                selectedCapacity = ">=0"
                minOrMax = "both"
                presNeeded = "no"
                view?.findViewById<NumberPicker>(R.id.nb_picker)?.visibility = View.INVISIBLE
            }
        }
    }
}