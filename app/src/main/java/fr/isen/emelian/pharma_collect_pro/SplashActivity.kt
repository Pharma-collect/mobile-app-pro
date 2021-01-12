package fr.isen.emelian.pharma_collect_pro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import android.view.WindowManager


class SplashActivity : AppCompatActivity() {

    var charSequence: CharSequence? = null
    var index = 0
    var delay: Long = 200
    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Get windows size fully fill
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Switching Activity at the end
        Handler().postDelayed({
            startActivity(
                Intent(this@SplashActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
//            customType(this@SplashActivity, "bottom-to-up")
            finish()
        }, 4000)*/
    }

    /*var runnable: Runnable = object : Runnable {
        override fun run() {
            if (index < charSequence!!.length) {
                handler.postDelayed(this, delay)
            }
        }
    }*/
}