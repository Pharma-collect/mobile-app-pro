package fr.isen.emelian.pharma_collect_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.isen.emelian.pharma_collect_pro.services.EnableHttps.handleSSLHandshake
import fr.isen.emelian.pharma_collect_pro.repository.UserRepository
import fr.isen.emelian.pharma_collect_pro.services.FileService
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"
    private val fileService: FileService =
        FileService()
    private val repository: UserRepository =
        UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btt = AnimationUtils.loadAnimation(this, R.anim.down_to_up)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        layout.startAnimation(btt)
    }

    fun onValidateClicked(view: View) {
        handleSSLHandshake()
        repository.logRequest(etUsername.text.toString(), etPassword.text.toString(), this@LoginActivity)

    }
}