package com.example.gencont_app.cours

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.R
import com.example.gencont_app.configDB.database.AppDatabase
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChapiterActivity : AppCompatActivity() {
    private lateinit var titleTextView: MaterialTextView
    private lateinit var descTextView: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chapiter)



        titleTextView = findViewById(R.id.tvChapitreTitle) // À créer dans ton XML
        descTextView = findViewById(R.id.tvChapitreContent) // À créer dans ton XML

        val coursId = intent.getLongExtra("cours_id", -1L)

        println("coursId"+coursId);

        if (coursId != -1L) {
            val db = AppDatabase.getInstance(applicationContext)
            CoroutineScope(Dispatchers.Main).launch {
                val cours = db.coursDao().getCoursById(coursId)
                cours?.let {
                    titleTextView.text = it.titre
                    descTextView.text = it.description
                }
            }
        }
    }
}