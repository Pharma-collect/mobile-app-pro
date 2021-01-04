package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PharmacyViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is pharmacy Fragment"
    }
    val text: LiveData<String> = _text
}