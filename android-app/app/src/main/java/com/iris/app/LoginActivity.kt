package com.iris.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.iris.app.data.LoginRequest
import com.iris.app.data.LoginResponse
import com.iris.app.data.SessionManager
import com.iris.app.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var loginProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sessionManager = SessionManager(this)
        if (!sessionManager.getAccessToken().isNullOrBlank()) {
            goToMain()
            return
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        loginProgress = findViewById(R.id.loginProgress)

        loginButton.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        val email = emailEditText.text?.toString()?.trim().orEmpty()
        val password = passwordEditText.text?.toString().orEmpty()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Informe e-mail e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val api = ApiClient.create(this)
        api.login(LoginRequest(email = email, password = password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    setLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        SessionManager(this@LoginActivity).saveAccessToken(response.body()!!.access)
                        goToMain()
                    } else {
                        val backendError = response.errorBody()?.string().orEmpty()
                        Toast.makeText(
                            this@LoginActivity,
                            "Falha no login (${response.code()}): $backendError",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro de conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun setLoading(loading: Boolean) {
        loginProgress.visibility = if (loading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !loading
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
