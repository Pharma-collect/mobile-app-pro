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
import kotlinx.android.synthetic.main.fragment_first_screen.view.*
import kotlinx.android.synthetic.main.fragment_fourth_screen.view.*
import kotlinx.android.synthetic.main.fragment_second_screen.view.*


class FourthScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fourth_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.next4.setOnClickListener {
            viewPager?.currentItem = 4
        }

        view.back4.setOnClickListener{
            viewPager?.currentItem = 2
        }

        view.skip4.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            context?.startActivity(intent)
            onBoradingFinished()
        }

        return view
    }

    private fun onBoradingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}