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

class CourSectionActivity : AppCompatActivity() {
    private lateinit var titleTextView: MaterialTextView
    private lateinit var descTextView: MaterialTextView
    private lateinit var tvChapitreContent: MaterialTextView
    private lateinit var example: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cour_section)

        titleTextView = findViewById(R.id.tvChapitreTitle)
        descTextView = findViewById(R.id.tvChapitreContent)
        tvChapitreContent = findViewById(R.id.tvChapitreContent)
        example = findViewById(R.id.example)

        val sectionId = intent.getLongExtra("section_id", -1L)

        if (sectionId != -1L) {
            val db = AppDatabase.getInstance(applicationContext)
            CoroutineScope(Dispatchers.Main).launch {
                val section = db.sectionDao().getById(sectionId)
                section?.let {
                    titleTextView.text = it.titre
                    descTextView.text = "Ordre : ${it.numeroOrder}"
                    tvChapitreContent.text = it.contenu
                    example.text = it.dateCreation ?: "Date inconnue"
                }
            }
        }
    }
}