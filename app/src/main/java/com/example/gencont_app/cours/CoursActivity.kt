package com.example.gencont_app.cours

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gencont_app.R
import com.example.gencont_app.configDB.data.Cours
import com.example.gencont_app.configDB.database.AppDatabase
import kotlinx.coroutines.launch

class CoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cours)

        val db = AppDatabase.getInstance(applicationContext)
        val coursDao = db.coursDao()

        val listView = findViewById<ListView>(R.id.lessonsListView)

        lifecycleScope.launch {
            val coursList = coursDao.getAllCours()

            // Conversion de List<Cours> -> List<Lesson>
            val lessons = coursList.map { cours ->
                Cours(
                    id = cours.id,
                    titre = cours.titre ?: "Titre inconnu",
                    description = cours.description ?: "Description indisponible",
                    nombreSection = cours.nombreSection?:0,
                    statusCours = cours.statusCours?:"Status inconnu"
                )
            }

            listView.adapter = LessonAdapter(
                context = this@CoursActivity,
                lessons = lessons,
                onStartLessonClick = { position ->
                    Toast.makeText(this@CoursActivity, "Lesson ${position + 1} clicked", Toast.LENGTH_SHORT).show()
                },
                onQuizClick = { position ->
                    Toast.makeText(this@CoursActivity, "Quiz ${position + 1} clicked", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
