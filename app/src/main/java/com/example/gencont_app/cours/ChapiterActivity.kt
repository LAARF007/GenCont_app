package com.example.gencont_app.cours

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gencont_app.R
import com.example.gencont_app.configDB.database.AppDatabase
import kotlinx.coroutines.launch

class ChapiterActivity : AppCompatActivity() {
//    private lateinit var titleTextView: MaterialTextView
//    private lateinit var descTextView: MaterialTextView
//    private lateinit var tvChapitreContent: MaterialTextView
//    private lateinit var example: MaterialTextView
private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chapiter)



//        titleTextView = findViewById(R.id.tvChapitreTitle) // À créer dans ton XML
//        descTextView = findViewById(R.id.tvChapitreContent) // À créer dans ton XML
//        tvChapitreContent = findViewById(R.id.tvChapitreContent) // À créer dans ton XML
//        example = findViewById(R.id.example) // À créer dans ton XML


//        val coursId = intent.getLongExtra("cours_id", -1L)
//
//        println("coursId"+coursId);



        val coursId = intent.getLongExtra("cours_id", -1)

        if (coursId != -1L) {
            val db = AppDatabase.getInstance(applicationContext)
            val sectionDao = db.sectionDao()

            listView = findViewById(R.id.sectionListView)

            lifecycleScope.launch {
                val sections = sectionDao.getSectionsForCours(coursId)
                listView.adapter = ChapiterAdapter(this@ChapiterActivity, sections)
            }
        }

//        if (coursId != -1L) {
//            val db = AppDatabase.getInstance(applicationContext)
//            CoroutineScope(Dispatchers.Main).launch {
//                val cours = db.sectionDao().getById(coursId)
//                cours?.let {
//                    titleTextView.text = it.titre
//                    descTextView.text = it.titre
//                    tvChapitreContent.text = it.contenu
//                    example.text = it.contenu
//
//                }
//            }
//        }
    }
}