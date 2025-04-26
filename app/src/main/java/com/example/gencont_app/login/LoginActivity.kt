package com.example.gencont_app.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.example.gencont_app.R
import com.example.gencont_app.formulaire.FormulaireActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initializing views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)



        // Handle login button click
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Proceed with login (authentication logic can be added here)
                loginUser(username, password)
                val intent = Intent(this, FormulaireActivity::class.java)
                startActivity(intent)
            } else {
                // Show error (for example, using a Toast)
            }
        }

        // Handle forgot password click
        tvForgotPassword.setOnClickListener {
            // Navigate to ForgotPasswordActivity or show a dialog
        }

        // Handle register click
        tvRegister.setOnClickListener {
            // Navigate to RegisterActivity
        }
    }

    private fun loginUser(username: String, password: String) {
        // Placeholder for login logic
        // Example: call a backend API, or check credentials in a local database
    }


}
