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
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    var backUrl = "https://88-122-235-110.traefik.me:61001/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btt = AnimationUtils.loadAnimation(this, R.anim.down_to_up)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        layout.startAnimation(btt)

    }

    private fun logRequest(username: String, password: String){
        val requestQueue = Volley.newRequestQueue(this)
        val url = "$backUrl/user_pro/loginPro"
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    var jsonResponse: JSONObject = JSONObject(response)
                    if (jsonResponse["success"] == true) {
                        intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@LoginActivity, "Wrong username or password", Toast.LENGTH_LONG).show()
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(this@LoginActivity, error.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Host"] = "node"
                    return params
                }

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username
                    params["password"] = password

                    return params
                }
            }
        requestQueue.add(stringRequest)
    }

    fun onValidateClicked(view: View) {
        handleSSLHandshake()
        logRequest(etUsername.text.toString(), etPassword.text.toString())
    }

}