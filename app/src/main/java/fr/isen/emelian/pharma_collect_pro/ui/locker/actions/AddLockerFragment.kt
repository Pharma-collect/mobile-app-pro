package fr.isen.emelian.pharma_collect_pro.ui.locker.actions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R

class AddLockerFragment : Fragment(), View.OnClickListener {

    private lateinit var amount: String
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_locker, container, false)

        var pickerAmount: NumberPicker = root.findViewById(R.id.picker_amount)

        pickerAmount.maxValue = 5
        pickerAmount.minValue = 1

        pickerAmount.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                amount = p2.toString()
                Log.d("aaaaaaaaa", amount)
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.cancel_locker_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cancel_locker_btn -> activity?.onBackPressed()
        }
    }

}