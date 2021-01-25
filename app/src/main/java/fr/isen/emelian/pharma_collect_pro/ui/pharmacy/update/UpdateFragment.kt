package fr.isen.emelian.pharma_collect_pro.ui.pharmacy.update

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.CollapsingToolbarLayout
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.FileService
import fr.isen.emelian.pharma_collect_pro.ui.home.HomeViewModel
import org.json.JSONObject
import java.io.File

class UpdateFragment : Fragment(), View.OnClickListener {

    private lateinit var updateviewmodel: UpdateViewModel
    private lateinit var navController: NavController
    private val fileService: FileService =
        FileService()
    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        updateviewmodel =
            ViewModelProvider(this).get(UpdateViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_update_pharmacy, container, false)

        val pharmaName: EditText = root.findViewById(R.id.pharma_name_update)
        val pharmaPhone: EditText = root.findViewById(R.id.pharma_phone_update)
        val pharmaRoadNb: EditText = root.findViewById(R.id.pharma_road_nb_update)
        val pharmaRoad: EditText = root.findViewById(R.id.pharma_road_update)
        val pharmaCity: EditText = root.findViewById(R.id.pharma_city_update)
        val pharmaPostcode: EditText = root.findViewById(R.id.pharma_postcode_update)

        updateviewmodel.pharmaName.observe(viewLifecycleOwner, Observer { pharmaName.setText(it) })
        updateviewmodel.pharmaPhone.observe(viewLifecycleOwner, Observer { pharmaPhone.setText(it) })
        updateviewmodel.pharmaRoad.observe(viewLifecycleOwner, Observer { pharmaRoad.setText(it) })
        updateviewmodel.pharmaRoadNumber.observe(viewLifecycleOwner, Observer { pharmaRoadNb.setText(it)})
        updateviewmodel.pharmaCity.observe(viewLifecycleOwner, Observer { pharmaCity.setText(it)})
        updateviewmodel.pharmaPostcode.observe(viewLifecycleOwner, Observer { pharmaPostcode.setText(it)})

        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.back_pharma_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.update_pharma_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.back_pharma_btn -> activity?.onBackPressed()
            R.id.update_pharma_btn -> updateviewmodel.updatePharmacy()
        }
    }
}