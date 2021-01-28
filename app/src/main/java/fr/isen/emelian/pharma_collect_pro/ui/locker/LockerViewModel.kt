package fr.isen.emelian.pharma_collect_pro.ui.locker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class LockerViewModel(application: Application) : AndroidViewModel(application), CoroutineScope by MainScope() {

}