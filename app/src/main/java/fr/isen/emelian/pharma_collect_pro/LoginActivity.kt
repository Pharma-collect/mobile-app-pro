package fr.isen.emelian.pharma_collect_pro

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import fr.isen.emelian.pharma_collect_pro.repository.UserRepository
import fr.isen.emelian.pharma_collect_pro.services.EnableHttps.handleSSLHandshake
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val repository: UserRepository =
        UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btt = AnimationUtils.loadAnimation(this, R.anim.down_to_up)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        layout.startAnimation(btt)
    }

    /**
     * On valide button click, ssl verification and connection function
     */
    fun onValidateClicked(view: View) {
        handleSSLHandshake()
        repository.logRequest(etUsername.text.toString(), etPassword.text.toString(), this@LoginActivity)
    }
}