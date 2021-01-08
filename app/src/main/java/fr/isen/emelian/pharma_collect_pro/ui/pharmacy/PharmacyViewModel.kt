package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import android.app.Application
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.isen.emelian.pharma_collect_pro.R
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.PharmacyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import fr.isen.emelian.pharma_collect_pro.services.FileService

class PharmacyViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    var myPharma: Pharmacy = Pharmacy()
    var myUser: User = User()
    private val pharmaRepository: PharmacyRepository = PharmacyRepository()
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    private val text1 = MutableLiveData<String>().apply { value = "" }
    private val text2 = MutableLiveData<String>().apply { value = "" }
    private val text3 = MutableLiveData<String>().apply { value = "" }
    private val text4 = MutableLiveData<String>().apply { value = "" }
    private val text5 = MutableLiveData<String>().apply { value = "" }
    private val text6 = MutableLiveData<String>().apply { value = "" }
    private val text7 = MutableLiveData<String>().apply { value = "" }
    private val text8 = MutableLiveData<String>().apply { value = "" }

    val pharma_name: LiveData<String> = text1
    val pharma_boss: LiveData<String> = text2
    val pharma_city: LiveData<String> = text3
    val pharma_postcode: LiveData<String> = text4
    val pharma_road: LiveData<String> = text5
    val pharma_roadnb: LiveData<String> = text6
    val pharma_phone: LiveData<String> = text7
    val pharma_shop: LiveData<String> = text8

    private val _text = MutableLiveData<String>().apply {
        value = "This is pharmacy Fragment"
    }
    val text: LiveData<String> = _text

    init {
        launch{
            myUser = fileService.getData(context)
            pharmaRepository.getPharmacyInfo(myUser.pharma_id.toString(), context)
            text1.value = "${myPharma.name}"
            text2.value = "${myPharma.boss}"
            text3.value = "${myPharma.city}"
            text4.value = "${myPharma.post_code}"
            text5.value = "${myPharma.road}"
            text6.value = "${myPharma.road_nb}"
            text7.value = "${myPharma.phone}"
            text8.value = "${myPharma.has_shop}"
        }
    }


}