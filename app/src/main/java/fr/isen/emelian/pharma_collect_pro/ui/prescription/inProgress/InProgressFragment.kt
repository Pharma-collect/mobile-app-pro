package fr.isen.emelian.pharma_collect_pro.ui.prescription.inProgress

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.isen.emelian.pharma_collect_pro.R

class InProgressFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = InProgressFragment()
    }

    private lateinit var viewModel: InProgressViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.in_progress_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InProgressViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.button_in_progress).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.button_in_progress -> activity?.onBackPressed()
        }
    }
}