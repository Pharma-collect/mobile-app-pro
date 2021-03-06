package fr.isen.emelian.pharma_collect_pro

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.isen.emelian.pharma_collect_pro.databinding.ActivityMainBinding
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding
    lateinit var id: String
    private val fileService: FileService = FileService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val fab : View = findViewById(R.id.fab)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        val bar: ActionBar? = actionBar
        bar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0E3C72")))

        var intent = intent
        id = intent.getStringExtra("id").toString()

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        fab.setOnClickListener {
            intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * When logout is clicked, it will disconnect and delete cache files
     */
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
}