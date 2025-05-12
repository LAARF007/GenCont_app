package com.example.gencont_app.score

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gencont_app.R
import com.example.gencont_app.quiz.QuizActivity
import com.google.android.material.progressindicator.CircularProgressIndicator

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)

        val score = intent.getIntExtra("score", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 0)
        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val incorrectAnswers = intent.getIntExtra("incorrectAnswers", 0)
        val sectionId = intent.getLongExtra("section_id", -1L)

        // Sauvegarder les données de quiz pour pouvoir les passer à l'activité de résumé
        val questionIds = intent.getLongArrayExtra("questionIds") ?: longArrayOf()
        val selectedAnswerIds = intent.getLongArrayExtra("selectedAnswerIds") ?: longArrayOf()

        // Initialiser les vues
        val scoreText = findViewById<TextView>(R.id.scoreText)
        val scoreText2 = findViewById<TextView>(R.id.scoreText2)
        val barProgress = findViewById<CircularProgressIndicator>(R.id.progress)
        val correctNumber = findViewById<TextView>(R.id.correctNumber)
        val incorrectNumber = findViewById<TextView>(R.id.incorrectNumber)
        val btnViewSummary = findViewById<Button>(R.id.btnViewSummary)
        val btnRetakeQuiz = findViewById<Button>(R.id.btnRetakeQuiz)

        // Afficher les résultats
        scoreText.text = "${score}%"
        scoreText2.text = "${score}%"
        barProgress.setProgress(score, true)
        correctNumber.text = correctAnswers.toString()
        incorrectNumber.text = incorrectAnswers.toString()

        // Gestion des clics sur les boutons
        btnViewSummary.setOnClickListener {
            // Ouvrir l'activité de résumé de quiz
            val intent = Intent(this, SummaryActivity::class.java).apply {
                putExtra("score", score)
                putExtra("totalQuestions", totalQuestions)
                putExtra("correctAnswers", correctAnswers)
                putExtra("incorrectAnswers", incorrectAnswers)
                putExtra("questionIds", questionIds)
                putExtra("selectedAnswerIds", selectedAnswerIds)
            }
            startActivity(intent)
        }

        btnRetakeQuiz.setOnClickListener {
            // Retourner à l'activité QuizActivity avec le même sectionId
            if (sectionId != -1L) {
                val intent = Intent(this, QuizActivity::class.java).apply {
                    putExtra("section_id", sectionId)
                    // Flag pour indiquer que c'est une reprise de quiz
                    putExtra("retake", true)
                }
                startActivity(intent)
            }
            finish() // Fermer l'activité actuelle
        }
    }
}