package com.example.gencont_app.score

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.R

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


        val score = intent.getIntExtra("score", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 0)
        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val incorrectAnswers = intent.getIntExtra("incorrectAnswers", 0)

        // Initialiser les vues
        val scoreText = findViewById<TextView>(R.id.scoreText)
        val correctNumber = findViewById<TextView>(R.id.correctNumber)
        val incorrectNumber = findViewById<TextView>(R.id.incorrectNumber)
        val btnViewSummary = findViewById<Button>(R.id.btnViewSummary)
        val btnRetakeQuiz = findViewById<Button>(R.id.btnRetakeQuiz)

        // Afficher les résultats
        scoreText.text = "Your Score: ${score}%"
        correctNumber.text = correctAnswers.toString()
        incorrectNumber.text = incorrectAnswers.toString()

        // Gestion des clics sur les boutons
        btnViewSummary.setOnClickListener {
            // Ajouter la logique pour afficher le résumé du quiz
        }

//        btnRetakeQuiz.setOnClickListener {
//            finish() // Retour au quiz
//        }
    }
}