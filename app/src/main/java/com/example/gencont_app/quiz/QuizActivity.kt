package com.example.gencont_app.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gencont_app.R
import com.example.gencont_app.configDB.database.AppDatabase
import com.example.gencont_app.score.ScoreActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    private lateinit var listViewQuestions: ListView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        listViewQuestions = findViewById(R.id.listViewQuestions)
        btnSubmit = findViewById(R.id.btnSubmit)

        val sectionId = intent.getLongExtra("section_id", -1L)

        if (sectionId != -1L) {
            val db = AppDatabase.getInstance(applicationContext)
            CoroutineScope(Dispatchers.Main).launch {
                val quiz = db.quizDao().getQuizBySection(sectionId)
                quiz?.let {
                    val questions = db.questionDao().getQuestionsWithReponses(it.id)
                    val adapter = QuestionAdapter(this@QuizActivity, questions)
                    listViewQuestions.adapter = adapter
                }
            }
        }


        btnSubmit.setOnClickListener {
            val adapter = listViewQuestions.adapter as QuestionAdapter
            val score = adapter.calculateScore()
            val totalQuestions = adapter.getTotalQuestions()

            Toast.makeText(
                this@QuizActivity,
                "Score: $score/$totalQuestions",
                Toast.LENGTH_LONG
            ).show()

            // Vous pouvez ajouter ici la logique pour afficher le résultat différemment

            val result = adapter.calculateDetailedResult()

            val intent = Intent(this, ScoreActivity::class.java).apply {
                putExtra("score", result.score)
                putExtra("totalQuestions", result.totalQuestions)
                putExtra("correctAnswers", result.correctAnswers)
                putExtra("incorrectAnswers", result.incorrectAnswers)
            }
            startActivity(intent)
        }
    }
}