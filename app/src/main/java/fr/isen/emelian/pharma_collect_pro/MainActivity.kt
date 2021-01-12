package fr.isen.emelian.pharma_collect_pro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import fr.isen.emelian.pharma_collect_pro.services.FileService

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    lateinit var id: String
    private val fileService: FileService = FileService()
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val fab : View = findViewById(R.id.fab)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_locker, R.id.navigation_prescription, R.id.navigation_shop, R.id.navigation_pharmacy))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var intent = intent
        id = intent.getStringExtra("id").toString()

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        fab.setOnClickListener {
            intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }


    fun onButtonLogoutClicked(view: View) {
        val deleteResponse = fileService.deleteData(this)
        if(deleteResponse.equals(true)){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "See you!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Error, cannot disconnect for the moment", Toast.LENGTH_LONG).show()
        }
    }

    fun onButtonHelpClicked(view: View) {
        val editor = prefs!!.edit()
        editor.putBoolean("Finished", false)
        editor.apply()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
    }

}