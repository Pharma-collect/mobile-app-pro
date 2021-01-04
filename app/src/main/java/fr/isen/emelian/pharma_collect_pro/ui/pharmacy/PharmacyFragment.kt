package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.emelian.pharma_collect_pro.R

class PharmacyFragment : Fragment() {

    companion object {
        fun newInstance() = PharmacyFragment()
    }

    private lateinit var viewModel: PharmacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pharmacy, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PharmacyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}