package com.example.gencont_app.register

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gencont_app.R
import com.example.gencont_app.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var nomEditText: EditText
    private lateinit var prenomEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Make sure the XML file is named correctly

        // Initialize views
        nomEditText = findViewById(R.id.idNom)
        prenomEditText = findViewById(R.id.idPrenom)
        emailEditText = findViewById(R.id.idEmail)
        passwordEditText = findViewById(R.id.idPassword)
        signUpButton = findViewById(R.id.btnSignUp)
        signInText = findViewById(R.id.tvSignIn)

        signUpButton.setOnClickListener {
            val nom = nomEditText.text.toString().trim()
            val prenom = prenomEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // You can add Firebase or SQLite logic here
                Toast.makeText(this, "Account created for $prenom $nom", Toast.LENGTH_SHORT).show()

                // Redirect to Login screen (optional)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
