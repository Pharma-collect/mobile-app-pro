package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.ui.home.HomeViewModel

class PharmacyFragment : Fragment() {

    companion object {
        fun newInstance() = PharmacyFragment()
    }

    private lateinit var pharmaViewModel: PharmacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pharmaViewModel =
            ViewModelProvider(this).get(PharmacyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        //val textView: TextView = root.findViewById(R.id.tvWelcomeBack)
        /*pharmaViewModel.welcomeText.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        /*val btt = AnimationUtils.loadAnimation(context, R.anim.up_to_down)
        val fade = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val layout: ConstraintLayout = root.findViewById(R.id.layout_header)
        layout.startAnimation(btt)*/

        val textView_name: TextView = root.findViewById(R.id.pharma_name)
        val textView_id: TextView = root.findViewById(R.id.pharma_id)
        val textView_road: TextView = root.findViewById(R.id.pharma_road_name)
        val textView_roadnb: TextView = root.findViewById(R.id.pharma_road_nb)
        val textView_postcode: TextView = root.findViewById(R.id.pharma_postcode)
        val textView_city: TextView = root.findViewById(R.id.pharma_city)
        val textView_phone: TextView = root.findViewById(R.id.pharma_phone)

       /* pharmaViewModel.pharma_name.observe(viewLifecycleOwner, Observer { textView_name.text = it })
        pharmaViewModel.pharma_road_name.observe(viewLifecycleOwner, Observer { textView_road.text = it })
        pharmaViewModel.pharma_road_nb.observe(viewLifecycleOwner, Observer { textView_roadnb.text = it })
        pharmaViewModel.pharma_postcode.observe(viewLifecycleOwner, Observer { textView_postcode.text = it })
        pharmaViewModel.pharma_city.observe(viewLifecycleOwner, Observer { textView_city.text = it })
        pharmaViewModel.pharma_phone.observe(viewLifecycleOwner, Observer { textView_phone.text = it })
        pharmaViewModel.pharma_id.observe(viewLifecycleOwner, Observer { textView_id.text = it })*/

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pharmaViewModel = ViewModelProvider(this).get(PharmacyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}