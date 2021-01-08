package fr.isen.emelian.pharma_collect_pro.ui.home

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.isen.emelian.pharma_collect_pro.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.repository.HomeRepository
import fr.isen.emelian.pharma_collect_pro.repository.PharmacyRepository
import fr.isen.emelian.pharma_collect_pro.services.FileService


class HomeViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private val id: String = "0"
    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    private val _welcomeText = MutableLiveData<String>().apply {
        value = "welcome back user $id"
    }
    val welcomeText: LiveData<String> = _welcomeText

    var myUser: User = User()
    private val homeRepository: HomeRepository = HomeRepository()
    private val pharmaRepository: PharmacyRepository = PharmacyRepository()
    private val fileService: FileService = FileService()
    private val context = getApplication<Application>().applicationContext

    init {
        launch{
            myUser = fileService.getData(context)
            //homeRepository.getUserInformations(myUser.id.toString(), context)
            //pharmaRepository.getPharmacyInfo(myUser.pharma_id.toString(), context)
            _welcomeText.value = "Welcome Back ${myUser.username}"
        }
    }

    fun onButtonClicked() {
        Log.d("HomeVM", "ButtonClicked")
        val deleteResponse = fileService.deleteData(context)
        if(deleteResponse.equals(true)){
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)
            Toast.makeText(context, "See you!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Error, cannot disconnect for the moment", Toast.LENGTH_LONG).show()
        }
    }
}