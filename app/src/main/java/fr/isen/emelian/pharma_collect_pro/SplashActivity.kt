package fr.isen.emelian.pharma_collect_pro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * This class will only support the splash view fragment
 * It will also (on the first connection) support the didactiel
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}