package fr.isen.emelian.pharma_collect_pro.ui.pharmacy

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.isen.emelian.pharma_collect_pro.dataClass.Pharmacy
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.PharmacyRepository
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.coroutines.*

class PharmacyViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private var myRepo: PharmacyRepository = PharmacyRepository()
    private var myUser: User = User()

    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    private val _name = MutableLiveData<String>()
    private val _city = MutableLiveData<String>()
    private val _postcode = MutableLiveData<String>()
    private val _road = MutableLiveData<String>()
    private val _roadnb = MutableLiveData<String>()
    private val _phone = MutableLiveData<String>()
    private val _id = MutableLiveData<String>()
    private val _shop = MutableLiveData<String>()
    private val _admin = MutableLiveData<String>()
    private val _user = MutableLiveData<String>()

    val name: LiveData<String> = _name
    val city: LiveData<String> = _city
    val postcode: LiveData<String> = _postcode
    val road_name: LiveData<String> = _road
    val road_nb: LiveData<String> = _roadnb
    val phone: LiveData<String> = _phone
    val id: LiveData<String> = _id
    val shop: LiveData<String> = _shop
    val admin: LiveData<String> = _admin
    val user: LiveData<String> = _user

    init {
        launch {
            myUser = fileService.getData(context)
            getPharmaData(myUser.pharma_id.toString(), context)
            getUsersData(myUser.pharma_id.toString(), context)
        }
    }

    /**
     * Get pharmacy information
     */
    private suspend fun getPharmaData(id: String, context: Context) {
        val response = myRepo.getPharmacyInfo(id, context)
        while(response.success != "true"){
            delay(5)
        }
        implInfo(response)
    }

    /**
     * Get amount of admin and user
     */
    private suspend fun getUsersData(id: String, context: Context){
        myRepo.countUserOfPharmacy(id, context)
        while (myRepo.numUsersAdmins["admins"] == null){
            delay(50)
            Log.d("PharmaInfo", myRepo.numUsersAdmins.toString())
        }
        _admin.value = myRepo.numUsersAdmins["admins"]
        _user.value = myRepo.numUsersAdmins["users"]
    }

    /**
     * Display pharmacy information
     */
    private fun implInfo(pharmacy: Pharmacy){
        _name.value = pharmacy.name
        _city.value = pharmacy.city
        _postcode.value = pharmacy.post_code
        if( pharmacy.has_shop.toString() == "0"){
            _shop.value = "No"
        } else {
            _shop.value = "Yes"
        }
        _road.value = pharmacy.road
        _roadnb.value = pharmacy.road_nb
        _phone.value = "0${pharmacy.phone}"
        _id.value = "ID : ${pharmacy.id}"
    }
}