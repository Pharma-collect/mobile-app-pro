package fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.emelian.pharma_collect_pro.LoginActivity
import fr.isen.emelian.pharma_collect_pro.R
import kotlinx.android.synthetic.main.fragment_second_screen.*
import kotlinx.android.synthetic.main.fragment_second_screen.view.*

class SecondScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second_screen, container, false)

        view.skip2.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            context?.startActivity(intent)
            onBoardingFinished()
        }

        view.imageView5.setOnClickListener {
            linear_prescription.visibility = View.VISIBLE
            linear_basic.visibility = View.INVISIBLE
            linear_locker.visibility = View.INVISIBLE
            linear_pharmacy.visibility = View.INVISIBLE
            linear_products.visibility = View.INVISIBLE
            linear_stats.visibility = View.INVISIBLE
        }

        view.imageView4.setOnClickListener {
            linear_prescription.visibility = View.INVISIBLE
            linear_basic.visibility = View.INVISIBLE
            linear_locker.visibility = View.VISIBLE
            linear_pharmacy.visibility = View.INVISIBLE
            linear_products.visibility = View.INVISIBLE
            linear_stats.visibility = View.INVISIBLE
        }

        view.imageView6.setOnClickListener {
            linear_prescription.visibility = View.INVISIBLE
            linear_basic.visibility = View.INVISIBLE
            linear_locker.visibility = View.INVISIBLE
            linear_pharmacy.visibility = View.INVISIBLE
            linear_products.visibility = View.VISIBLE
            linear_stats.visibility = View.INVISIBLE
        }

        view.imageView7.setOnClickListener {
            linear_prescription.visibility = View.INVISIBLE
            linear_basic.visibility = View.INVISIBLE
            linear_locker.visibility = View.INVISIBLE
            linear_pharmacy.visibility = View.VISIBLE
            linear_products.visibility = View.INVISIBLE
            linear_stats.visibility = View.INVISIBLE
        }

        view.imageView3.setOnClickListener {
            linear_prescription.visibility = View.INVISIBLE
            linear_basic.visibility = View.INVISIBLE
            linear_locker.visibility = View.INVISIBLE
            linear_pharmacy.visibility = View.INVISIBLE
            linear_products.visibility = View.INVISIBLE
            linear_stats.visibility = View.VISIBLE
        }

        return view
    }

    /**
     * Function which change the share pref if the on boarding is finished or skip
     */
    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}