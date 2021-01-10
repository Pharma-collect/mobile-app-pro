package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.isen.emelian.pharma_collect_pro.LoginActivity
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

    /*private val tv_pharma_name = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_boss = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_city = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_postcode = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_road = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_roadnb = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_phone = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_shop = MutableLiveData<String>().apply { value = "" }
    private val tv_pharma_id = MutableLiveData<String>().apply { value = "" }

    val pharma_name: LiveData<String> = tv_pharma_name
    val pharma_city: LiveData<String> = tv_pharma_city
    val pharma_postcode: LiveData<String> = tv_pharma_postcode
    val pharma_road_name: LiveData<String> = tv_pharma_road
    val pharma_road_nb: LiveData<String> = tv_pharma_roadnb
    val pharma_phone: LiveData<String> = tv_pharma_phone
    val pharma_id: LiveData<String> = tv_pharma_id*/

    private val _text = MutableLiveData<String>().apply {
        value = "This is pharmacy Fragment"
    }
    val text: LiveData<String> = _text

    init {
        launch{
            /*myUser = fileService.getData(context)
            pharmaRepository.getPharmacyInfo(myUser.pharma_id.toString(), context)
            tv_pharma_name.value = "${myPharma.name}"
            tv_pharma_city.value = "${myPharma.city}"
            tv_pharma_postcode.value = "${myPharma.post_code}"
            tv_pharma_road.value = "${myPharma.road}"
            tv_pharma_roadnb.value = "${myPharma.road_nb}"
            tv_pharma_phone.value = "${myPharma.phone}"
            tv_pharma_id.value = "${myPharma.id}"*/
        }
    }
}