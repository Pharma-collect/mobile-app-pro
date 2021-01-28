package fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import fr.isen.emelian.pharma_collect_pro.LoginActivity
import fr.isen.emelian.pharma_collect_pro.R
import kotlinx.android.synthetic.main.fragment_third_screen.view.*

class ThirdScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.next3.setOnClickListener {
            viewPager?.currentItem = 3
        }

        view.back3.setOnClickListener{
            viewPager?.currentItem = 1
        }

        view.skip3.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            context?.startActivity(intent)
            onBoardingFinished()
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