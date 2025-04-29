package com.example.gencont_app.cours
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.gencont_app.R
import com.example.gencont_app.configDB.data.Cours

class LessonAdapter(
    private val context: Context,
    private val lessons: List<Cours>,
    private val onStartLessonClick: (Int) -> Unit,
    private val onQuizClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = lessons.size
    override fun getItem(position: Int): Any = lessons[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_lesson, parent, false)

        val lesson = lessons[position]

        view.apply {
            findViewById<TextView>(R.id.tvLessonTitle).text = lesson.titre
            findViewById<TextView>(R.id.tvLessonDesc).text = lesson.description


            findViewById<Button>(R.id.btnStartLesson).setOnClickListener {
                // ✅ Démarrer l'activité ChapitreActivity et passer l'ID
                val intent = Intent(context, ChapiterActivity::class.java)
                intent.putExtra("cours_id", lesson.id ?: -1)
                context.startActivity(intent)
            }

            findViewById<Button>(R.id.btnQuiz).setOnClickListener {
                onQuizClick(position)
            }
        }

        return view
    }
}