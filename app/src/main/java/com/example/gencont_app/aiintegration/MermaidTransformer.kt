import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MermaidTransformer {

    companion object {
        private const val TAG = "MermaidTransformer"
        private const val ENDPOINT = "https://models.inference.ai.azure.com/chat/completions"
        private const val API_KEY = "key"  // Utilisez votre clé API
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private val client = OkHttpClient()
    }

    /**
     * Transforme le contenu d'une section en diagramme Mermaid
     * @param sectionContent le contenu textuel de la section
     * @param sectionTitle le titre de la section (pour le contexte)
     * @return le code Mermaid généré
     */
    suspend fun transformToMermaid(sectionContent: String, sectionTitle: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Transforme le contenu éducatif suivant en un diagramme Mermaid pertinent. 
            Choisis le type de diagramme le plus approprié selon le contenu (flowchart, sequence, class, etc.).
            Titre de la section: $sectionTitle
            
            Contenu:
            $sectionContent
            
            Renvoie uniquement le code Mermaid sans aucune explication ni préambule, pas de triple backticks.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("model", "DeepSeek-V3-0324") // Ajustez selon votre modèle
            put("temperature", 0.3)
            put("messages", JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("api-key", API_KEY)
            .addHeader("Content-Type", "application/json")
            .post(requestJson.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        var result = ""
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                result = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            } else {
                Log.e(TAG, "API request failed: ${response.code} - $responseBody")
                result = "Error: ${response.code}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in API call", e)
            result = "Error: ${e.message}"
        }

        return@withContext result
    }
}