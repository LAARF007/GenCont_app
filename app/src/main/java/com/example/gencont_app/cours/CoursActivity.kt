package com.example.gencont_app.cours

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.gencont_app.R

class CoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cours)
        // Préparation des données
        val lessons = listOf(
            Lesson(
                "Lesson 1: Introduction to AI",
                "Discover the basics of Artificial Intelligence..."
            ),
            Lesson(
                "Lesson 2: Machine Learning",
                "Explore the fundamentals of Machine Learning..."
            ),
            Lesson(
                "Lesson 3: Neural Networks",
                "Understand the architecture of neural networks..."
            )
        )



        // Configuration de l'Adapter
        val listView = findViewById<ListView>(R.id.lessonsListView)
        listView.adapter = LessonAdapter(
            context = this,
            lessons = lessons,
            onStartLessonClick = { position ->
                // Ouvrir la leçon (ex: navigation vers un écran)
                Toast.makeText(this, "Lesson ${position + 1} clicked", Toast.LENGTH_SHORT).show()
            },
            onQuizClick = { position ->
                // Ouvrir le quiz
                Toast.makeText(this, "Quiz ${position + 1} clicked", Toast.LENGTH_SHORT).show()
            }
        )
    }
}