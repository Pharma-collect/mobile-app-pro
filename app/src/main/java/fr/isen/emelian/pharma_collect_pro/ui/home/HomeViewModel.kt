package fr.isen.emelian.pharma_collect_pro.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import fr.isen.emelian.pharma_collect_pro.services.MyRepository
import org.json.JSONObject
import java.io.File


class HomeViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

    private val id: String = "0"

    private val _welcomeText = MutableLiveData<String>().apply {
        value = "welcome back user $id"
    }
    val welcomeText: LiveData<String> = _welcomeText

    var user: User = User()
    private val repository: MyRepository = MyRepository()
    private val context = getApplication<Application>().applicationContext

    init {
        launch{
            getData()
            repository.getUserInformations(context, user)
            _welcomeText.value = "Welcome Back ${user.username}"
            Log.d("HomeVM", "Welcome Back ${user.username}")
            Log.d("HomeVM", "My Token :  ${user.token}")
        }
    }

    fun onButtonClicked(){
        Log.d("HomeVM", "button was clicked")
    }

    /*
     * Read cache file which get informations
     * To find file : "Device File Explorer" --> data --> data --> fr.isen.emelian .. --> cacheData_user.json
     */
    private fun getData() {
        val datas: String = File(context.cacheDir.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            user.id = jsonObject.optString("id")
            user.token = jsonObject.optString("token")
            user.username = jsonObject.optString("username")
            user.pharma_id = jsonObject.optString("pharmaId")
        }
    }
}