package fr.isen.emelian.pharma_collect_pro.ui.locker.actions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.repository.LockerRepository
import kotlinx.android.synthetic.main.dialog_confirmation_locker.view.*

class AddLockerFragment : Fragment(), View.OnClickListener {

    private var amount: String = "1"
    private lateinit var navController: NavController
    private val lockerRepository: LockerRepository = LockerRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_locker, container, false)

        val pickerAmount: NumberPicker = root.findViewById(R.id.picker_amount)

        pickerAmount.maxValue = 5
        pickerAmount.minValue = 1

        pickerAmount.setOnValueChangedListener { _, _, p2 -> amount = p2.toString() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.cancel_locker_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.add_locker_btn).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cancel_locker_btn -> activity?.onBackPressed()
            R.id.add_locker_btn -> changeFragmentAfterAddition()
        }
    }

    /**
     * Fragment switch back after addition of a locker
     */
    @SuppressLint("SetTextI18n")
    private fun changeFragmentAfterAddition(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val navView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_clear, null)
        val question: TextView = navView.findViewById(R.id.sure_label)
        question.text = "Add $amount container(s) to the pharmacy ?"
        builder.setView(navView)
        val alertDialog = builder.create()
        alertDialog.show()

        navView.button_confirm.setOnClickListener {
            context?.let { lockerRepository.addContainer(amount, it) }
            Handler().postDelayed({
                alertDialog.dismiss()
                activity?.onBackPressed()
            }, 1000)

        }

        navView.button_cancel.setOnClickListener {
            Toast.makeText(context, "Operation canceled", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }
}