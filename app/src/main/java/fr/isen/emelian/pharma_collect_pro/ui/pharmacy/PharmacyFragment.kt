package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.databinding.FragmentPharmacyBinding

class PharmacyFragment : Fragment(), View.OnClickListener {

    private lateinit var pharmaViewModel: PharmacyViewModel
    private lateinit var binding: FragmentPharmacyBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pharmacy,
            container,
            false
        )

        pharmaViewModel =
            ViewModelProvider(this).get(PharmacyViewModel::class.java)

        binding.pharmaviewmodel = pharmaViewModel

        val root = inflater.inflate(R.layout.fragment_pharmacy, container, false)

        val name: TextView = root.findViewById(R.id.pharma_name)
        val id: TextView = root.findViewById(R.id.pharma_id)
        val road: TextView = root.findViewById(R.id.pharma_road_name)
        val roadnb: TextView = root.findViewById(R.id.pharma_road_nb)
        val postcode: TextView = root.findViewById(R.id.pharma_postcode)
        val city: TextView = root.findViewById(R.id.pharma_city)
        val phone: TextView = root.findViewById(R.id.pharma_phone)
        val shop: TextView = root.findViewById(R.id.profile_has_shop)
        val admin: TextView = root.findViewById(R.id.pharma_number_admin)
        val user: TextView = root.findViewById(R.id.pharma_number_user)

        pharmaViewModel.name.observe(viewLifecycleOwner, Observer { name.text = it })
        pharmaViewModel.road_name.observe(viewLifecycleOwner, Observer { road.text = it })
        pharmaViewModel.road_nb.observe(viewLifecycleOwner, Observer { roadnb.text = it })
        pharmaViewModel.postcode.observe(viewLifecycleOwner, Observer { postcode.text = it })
        pharmaViewModel.city.observe(viewLifecycleOwner, Observer { city.text = it })
        pharmaViewModel.phone.observe(viewLifecycleOwner, Observer { phone.text = it })
        pharmaViewModel.id.observe(viewLifecycleOwner, Observer { id.text = it })
        pharmaViewModel.shop.observe(viewLifecycleOwner, Observer { shop.text = it })
        pharmaViewModel.admin.observe(viewLifecycleOwner, Observer { admin.text = it })
        pharmaViewModel.user.observe(viewLifecycleOwner, Observer { user.text = it })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pharmaViewModel = ViewModelProvider(this).get(PharmacyViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.update_pharma_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.update_pharma_btn -> navController.navigate(R.id.action_navigation_pharmacy_to_navigation_update)
        }
    }
}