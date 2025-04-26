package com.example.gencont_app.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.R
import android.widget.Toast


class QuizActivity : AppCompatActivity() {
    private lateinit var listViewQuestions: ListView
    private lateinit var btnSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)


        listViewQuestions = findViewById(R.id.listViewQuestions)
        btnSubmit = findViewById(R.id.btnSubmit)

        val questions = listOf(
            Question("What is the capital of France?", listOf("Paris", "Berlin", "Madrid", "Rome")),
            Question("What is the capital of France?", listOf("Paris", "Berlin", "Madrid", "Rome"))
        )

        val adapter = QuestionAdapter(this, questions)
        listViewQuestions.adapter = adapter

        btnSubmit.setOnClickListener {
            // À toi de récupérer les réponses ici
            Toast.makeText(this, "Submit clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}