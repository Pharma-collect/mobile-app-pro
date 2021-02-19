package fr.isen.emelian.pharma_collect_pro.ui.prescription.orderMaking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.diegodobelo.expandingview.ExpandingItem
import com.diegodobelo.expandingview.ExpandingList
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.IDs
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.OrderRepository
import fr.isen.emelian.pharma_collect_pro.repository.PrescriptionRepository
import fr.isen.emelian.pharma_collect_pro.repository.ProductRepository
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*
import kotlinx.android.synthetic.main.dialog_picture.view.*
import kotlinx.android.synthetic.main.dialog_product_amount.view.*
import kotlinx.android.synthetic.main.fragment_product_selection.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class ProductSelectionFragment : Fragment(), View.OnClickListener {

    private var mExpandingList: ExpandingList? = null
    private var amount = 0
    private var myProductsOrder: JSONArray = JSONArray()
    private var myFinalArray: JSONArray = JSONArray()

    private var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private lateinit var idOrder: IDs
    private lateinit var orderId: String
    private lateinit var navController: NavController
    private var myUser: User = User()
    private var selected: Int = 0

    private val prescriptionRepository: PrescriptionRepository = PrescriptionRepository()
    private val orderRepository: OrderRepository = OrderRepository()
    private val productRepository: ProductRepository = ProductRepository()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idOrder = arguments!!.getParcelable("order_id")!!
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_product_selection, container, false)
        orderId = idOrder.id.toString()
        mExpandingList = view.findViewById(R.id.list_product)
        view.findViewById<Button>(R.id.button_search)
                .setOnClickListener {
                    mExpandingList!!.removeAllViews()
                    setView(view, view.findViewById<EditText>(R.id.et_search).text.toString())
                }

        setView(view, "")
        view.findViewById<TextView>(R.id.amount_product).text = "Amount of product : $amount"
        return view
    }

    private fun createItems(title: String, url: String, id: String, description: String, price: String, quantity: String, current: String, presNeeded: String) {

        val capacity: String =
            if(quantity == "null"){
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
            url,
            id,
            price,
            current,
            capacity
        )
    }

    @SuppressLint("SetTextI18n")
    private fun addItem(title: String, subItems: Array<String>, colorRes: Int, url: String, ids: String, price: String, current: String, quantity: String) {
        val item = mExpandingList!!.createNewItem(R.layout.expanding_layout)
        val myUri: Uri = Uri.parse(url)

        if (item != null) {
            item.setIndicatorColorRes(colorRes)
            (item.findViewById(R.id.title) as TextView).text = title
            if(current != "0") {
                (item.findViewById(R.id.add_more_sub_items) as Button).setBackgroundResource(R.drawable.ic_baseline_check_circle_24_green)
            }
            (item.findViewById(R.id.et_amount) as TextView).text = "Selected : $current"
            context?.let { Glide.with(it).load(myUri).into((item.findViewById(R.id.image) as ImageView)) }

            item.createSubItems(subItems.size)
            for (i in 0 until item.subItemsCount) {
                val view = item.getSubItemView(i)

                configureSubItem(view, subItems[i])

                item.findViewById<Button>(R.id.add_more_sub_items)
                    .setOnClickListener {
                        addProduct(item, ids, title, price, quantity)
                    }
            }
        }
    }

    private fun configureSubItem(view: View, subTitle: String) {
        (view.findViewById(R.id.sub_title) as TextView).text = subTitle
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun addProduct(item: ExpandingItem?, id: String, title: String, price: String, quantity: String) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_product_amount, null)
        val pickerState: NumberPicker = navView.findViewById(R.id.picker)

        if(quantity == "null"){
            pickerState.maxValue = 0
        } else {
            pickerState.maxValue = quantity.toInt()
        }
        pickerState.minValue = 0

        pickerState.setOnValueChangedListener { _, _, p2 -> selected = p2 }

        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.confirm.setOnClickListener {

            val number = selected
            val product = JSONObject()
            var amount = 0
            var occurence = 0

            product.put("id_product", id.toInt())
            product.put("quantity", number)
            product.put("title", title)
            product.put("price", price.toFloat())

            if(this.myProductsOrder.length() == 0) {
                this.myProductsOrder.put(product)
            } else {
                for (i in 0 until this.myProductsOrder.length()) {
                    val items = this.myProductsOrder.getJSONObject(i)
                    if (items["id_product"].toString() == id) {
                        occurence++
                        items.put("quantity", number)
                    }
                }
                if(occurence == 0) {
                    this.myProductsOrder.put(product)
                }
            }

            item?.findViewById<TextView>(R.id.et_amount)?.text = "Selected : $number"
            for (i in 0 until this.myProductsOrder.length()) {
                val items = this.myProductsOrder.getJSONObject(i)
                val nb = items["quantity"].toString()
                if(nb == "0"){
                    amount += nb.toInt()
                    (item?.findViewById(R.id.add_more_sub_items) as Button).setBackgroundResource(R.drawable.ic_baseline_add_circle_24)
                } else {
                    amount += nb.toInt()
                    (item?.findViewById(R.id.add_more_sub_items) as Button).setBackgroundResource(R.drawable.ic_baseline_check_circle_24_green)
                }
            }
            this.amount = amount
            view?.findViewById<TextView>(R.id.amount_product)?.text = "Amount of products :  " + this.amount
            alertDialog.dismiss()
            selected = 0

        }

        navView.cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }


    private fun setView(view: View, text: String) {
        val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            myUser.token = jsonObject.optString("token")
            myUser.pharma_id = jsonObject.optInt("pharmaId")
        }

        (view.findViewById(R.id.progressBar) as ProgressBar).visibility = View.VISIBLE

        val requestQueue = Volley.newRequestQueue(context)
        val url = "$backUrl/product/getProductsByPharmacy"
        val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        val jsonArray = jsonResponse.optJSONArray("result")

                        for (i in 0 until jsonArray.length()) {
                            var current = "0"
                            val item = jsonArray.getJSONObject(i)
                            if(item["title"].toString().contains(text, ignoreCase = true)){
                                if(this.myProductsOrder.length() != 0) {
                                    for (iteration in 0 until this.myProductsOrder.length()) {
                                        val itemProduct = this.myProductsOrder.getJSONObject(iteration)
                                        if (itemProduct["id_product"].toString() == item["id"].toString()) {
                                            current = itemProduct["quantity"].toString()
                                        }
                                    }
                                }
                                createItems(item["title"].toString(), item["image_url"].toString(), item["id"].toString(),
                                    item["description"].toString(), item["price"].toString(), item["capacity"].toString(),
                                    current, item["prescription_only"].toString())
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

    @SuppressLint("SetTextI18n")
    private fun openDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_add_product, null)
        val editTextNote: EditText = navView.findViewById(R.id.not_ed)
        val order: TextView = navView.findViewById(R.id.product_id)
        var myTotalPrice = 0.0

        val listProduct: MutableList<String> = ArrayList()
        for (i in 0 until this.myProductsOrder.length()) {
            val itemProduct = this.myProductsOrder.getJSONObject(i)
            if(itemProduct["quantity"].toString() != "0"){
                listProduct.add(itemProduct["title"].toString() + " x" + itemProduct["quantity"].toString())
                myTotalPrice += itemProduct["price"].toString().toFloat() * itemProduct["quantity"].toString().toFloat()
            }
        }
        val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listProduct) }
        val listId: ListView = navView.findViewById(R.id.list_view)
        val price: TextView = navView.findViewById(R.id.total_price)

        order.text = "Order id : $orderId"
        price.text = "Total price : " + String.format("%.2f", myTotalPrice) + "€"
        listId.adapter = adapter
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_confirm.setOnClickListener {

            for (i in 0 until this.myProductsOrder.length()) {
                val itemProduct = this.myProductsOrder.getJSONObject(i)
                if(itemProduct["quantity"].toString() != "0"){
                    itemProduct.remove("title")
                    itemProduct.remove("price")
                    this.myFinalArray.put(itemProduct)
                }
            }

            val datas: String = File(context?.cacheDir?.absolutePath + "Data_user.json").readText()
            if (datas.isNotEmpty()) {
                val jsonObject = JSONObject(datas)
                myUser.id = jsonObject.optInt("id")
                myUser.pharma_id = jsonObject.optInt("pharmaId")
            }

            val detail = editTextNote.text.toString()
            if(editTextNote.text.toString() != "") {
                context?.let { it1 -> orderRepository.getClientId(orderId,
                    myUser.pharma_id.toString(),
                    myUser.pharma_id.toString(),
                    myTotalPrice.toString(),
                    this.myFinalArray,
                    myUser.id.toString(),
                    detail,
                    it1)
                }
                context?.let { it1 -> productRepository.updateProductsofArray(this.myFinalArray, it1) }
                Toast.makeText(context, "Order successfully updated", Toast.LENGTH_LONG).show()
            } else {
                context?.let { it1 -> orderRepository.getClientId(orderId,
                    myUser.pharma_id.toString(),
                    myUser.pharma_id.toString(),
                    myTotalPrice.toString(),
                    this.myFinalArray,
                    myUser.id.toString(),
                    "ras",
                    it1)
                }
                context?.let { it1 -> productRepository.updateProductsofArray(this.myFinalArray, it1) }
                Toast.makeText(context, "Order successfully updated", Toast.LENGTH_LONG).show()
            }
            Handler().postDelayed({
                alertDialog.dismiss()
                navController.navigate(R.id.action_selectProductFragment_to_navigation_prescription)
            }, 1000)
        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }

    private fun switchToPicture() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_picture, null)
        val picture = navView.findViewById<ImageView>(R.id.pres_big)
        context?.let { prescriptionRepository.getOrderByIdPres(picture, orderId, myUser.token.toString(), it) }
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_back.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back).setOnClickListener(this)
        view.findViewById<Button>(R.id.set_ready).setOnClickListener(this)
        view.findViewById<Button>(R.id.see_prescription).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back -> activity?.onBackPressed()
            R.id.set_ready -> openDialog()
            R.id.see_prescription -> switchToPicture()
        }
    }
}
