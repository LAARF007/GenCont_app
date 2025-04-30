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
import com.example.gencont_app.cours.ChapiterActivity
import com.example.gencont_app.quiz.QuizActivity
class LessonAdapter(
    private val context: Context,
    lessons: List<Cours>, // Liste originale
    private val onStartLessonClick: (Int) -> Unit,
    private val onQuizClick: (Int) -> Unit
) : BaseAdapter() {

    // On inverse la liste pour afficher le dernier en premier
    private val sortedLessons = lessons.sortedByDescending { it.id }

    // Le premier élément (le dernier inséré en DB)
    private val lastLessonId: Long? = sortedLessons.firstOrNull()?.id

    override fun getCount(): Int = sortedLessons.size
    override fun getItem(position: Int): Any = sortedLessons[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_lesson, parent, false)

        val lesson = sortedLessons[position]

        val titleText = view.findViewById<TextView>(R.id.tvLessonTitle)
        val descText = view.findViewById<TextView>(R.id.tvLessonDesc)

        titleText.text = lesson.titre
        descText.text = lesson.description

        // 🎨 Mise en évidence du premier (le plus récent)
        if (lesson.id == lastLessonId) {
            view.setBackgroundColor(context.getColor(R.color.tag_color)) // ou une autre couleur
        } else {
            view.setBackgroundColor(context.getColor(android.R.color.white))
        }

        view.findViewById<Button>(R.id.btnStartLesson).setOnClickListener {
            val intent = Intent(context, ChapiterActivity::class.java)
            intent.putExtra("cours_id", lesson.id)
            context.startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnQuiz).setOnClickListener {
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra("cours_id", lesson.id)
            context.startActivity(intent)
        }

        return view
    }
}