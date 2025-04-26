
import com.example.gencont_app.configDB.database.AppDatabase
import com.example.gencont_app.configDB.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class CoursePersister(private val db: AppDatabase) {

    /**
     * Extrait le JSON retourné par l'API et le persiste dans toutes les tables.
     * @param jsonResponse la chaîne JSON brute contenant {"cours":{...}}
     * @param userId       l'id de l'utilisateur qui a généré ce prompt
     * @param promptStatus un status à stocker pour Prompt (ex: "DONE")
     */
    suspend fun saveCourse(
        jsonResponse: String,
        userId: Long,
        promptStatus: String = "DONE"
    ) = withContext(Dispatchers.IO) {
        // 1) Sanitize : retirer fences Markdown si présentes
        val sanitized = jsonResponse.trim().let { raw ->
            if (raw.startsWith("```")) {
                // supprime la première et la dernière ligne
                raw
                    .lines()
                    .drop(1)
                    .dropLast(1)
                    .joinToString("\n")
            } else {
                raw
            }
        }
        // 2) Construire le JSONObject sur le JSON pur
        val root = JSONObject(sanitized).getJSONObject("cours")
        val titre     = root.getString("titre")
        val niveau    = root.optString("niveau")
        val desc      = root.optString("description")
        val nSections = root.getInt("nombreSection")

        // 1) Sauvegarde du Prompt
        val prompt = Prompt(
            Tags       = listOf(niveau ?: ""),
            coursName  = titre,
            niveau     = niveau,
            langue     = null,
            description= desc,
            status_user= promptStatus,
            utilisateurId = userId
        )
        val promptId = db.promptDao().insert(prompt)

        // 2) Sauvegarde du Cours
        val cours = Cours(
            titre         = titre,
            description   = desc,
            nombreSection = nSections,
            statusCours   = "CREATED"
        )
        val coursId = db.coursDao().insert(cours)

        // 3) Parcours des sections
        val sections = root.getJSONArray("sections")
        for (i in 0 until sections.length()) {
            val secJson = sections.getJSONObject(i)
            val section = Section(
                titre       = secJson.getString("titre"),
                contenu     = secJson.getString("contenu"),
                numeroOrder = i + 1,
                coursId     = coursId
            )
            val sectionId = db.sectionDao().insert(section)

            // 4) Création du Quiz lié à cette section
            val quiz = Quiz(
                ref            = UUID.randomUUID().toString(),
                lib            = section.titre,
                nb_rep_correct = 1,
                score          = 0.0
            )
            val quizId = db.quizDao().insert(quiz)

            // 5) Lien Quiz ↔ Section
            db.quizSectionDao().insert(
                QuizSection(quizId = quizId, sectionId = sectionId)
            )

            // 6) Parcours des questions
            val questions = secJson.getJSONArray("quiz")
            for (j in 0 until questions.length()) {
                val qJson = questions.getJSONObject(j)
                // 6a) Insert Question
                val question = Question(
                    ref          = UUID.randomUUID().toString(),
                    libelle      = qJson.getString("libelle"),
                    status_question = "NEW"
                )
                val questionId = db.questionDao().insert(question)

                // 6b) Lien Quiz ↔ Question
                db.quizQuestionDao().insert(
                    QuizQuestion(quizId = quizId, questionId = questionId)
                )

                // 6c) Parcours des réponses
                val reps = qJson.getJSONArray("reponses")
                for (k in 0 until reps.length()) {
                    val rJson = reps.getJSONObject(k)
                    val rep = Reponse(
                        ref         = UUID.randomUUID().toString(),
                        lib         = rJson.getString("lib"),
                        status      = if (rJson.getBoolean("isCorrect")) "correct" else "incorrect",
                        questionId  = questionId
                    )
                    db.reponseDao().insert(rep)
                }
            }
        }
    }
}
