package fr.isen.emelian.pharma_collect_pro.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is history Fragment"
    }
    val text: LiveData<String> = _text
}