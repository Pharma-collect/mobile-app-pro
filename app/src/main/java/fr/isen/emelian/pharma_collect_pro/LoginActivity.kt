package fr.isen.emelian.pharma_collect_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import fr.isen.emelian.pharma_collect_pro.services.EnableHttps.handleSSLHandshake
import fr.isen.emelian.pharma_collect_pro.repository.LoginRepository
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private val repository: LoginRepository =
        LoginRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btt = AnimationUtils.loadAnimation(this, R.anim.down_to_up)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        layout.startAnimation(btt)
    }

    fun onValidateClicked(view: View) {
        handleSSLHandshake()
        //When testing server up
        //repository.logRequest(etUsername.text.toString(), etPassword.text.toString(), this@LoginActivity)
        //When testing design server down
        if(etUsername.text.toString().isEmpty() && etPassword.text.toString().isEmpty()){
            startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

    }
}