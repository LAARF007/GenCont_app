package com.example.gencont_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.configDB.data.Utilisateur
import com.example.gencont_app.configDB.database.AppDatabase
import com.example.gencont_app.formulaire.FormulaireActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // start **********Mohammed Ezzaim***********
        val db = AppDatabase.getInstance(applicationContext)
        val utilisateurDao = db.utilisateurDao()

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

        val intent = Intent(this, FormulaireActivity::class.java)
        startActivity(intent)
        // end **********Mohammed Ezzaim***********

    }
}