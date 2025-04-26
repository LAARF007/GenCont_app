package com.example.gencont_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.api.ChatApiClient
import com.example.gencont_app.configDB.data.Utilisateur
import com.example.gencont_app.configDB.database.AppDatabase
import com.example.gencont_app.formulaire.FormulaireActivity
import com.example.gencont_app.login.LoginActivity
import com.example.gencont_app.register.RegisterActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ensure the filename is activity_main.xml

        val db = AppDatabase.getInstance(applicationContext)
        val utilisateurDao = db.utilisateurDao()

//        this is a test
        runBlocking {
            launch {
                // Insert a Utilisateur
                val newUtilisateur = Utilisateur(
                    nom = "John",
                    prénom = "Doe",
                    email = "john.doe@example.com",
                    motDePasse = "password123"
                )
                val utilisateurId = utilisateurDao.insert(newUtilisateur)
                Log.d("DB_INIT", "Utilisateur inserted with ID: $utilisateurId")
            }
        }

// Par exemple dans onCreate ou après un clic de bouton :


        // to go directly to form (incomment this)
        /*  val intent = Intent(this, FormulaireActivity::class.java)
       startActivity(intent)*/

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
//            ChatApiClient.sendMessage("Explique-moi comment utiliser TabLayout + ViewPager2 en Kotlin")

            ChatApiClient.generateCourseJson(
                titre       = "machine learning",
                niveau      = "débutant",
                description = "c'est quoi le machine learning",
                emotion     = "happy"
            )

        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }
}
