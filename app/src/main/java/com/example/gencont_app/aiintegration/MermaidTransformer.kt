import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

class MermaidTransformer {

    companion object {
        private const val TAG = "MermaidTransformer"
        private const val ENDPOINT = "https://models.inference.ai.azure.com/chat/completions"
        private const val API_KEY = "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"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private val client = OkHttpClient()
    }

    suspend fun transformToMermaid(sectionContent: String, sectionTitle: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Transforme le contenu éducatif suivant en un diagramme Mermaid pertinent. 
            Choisis le type de diagramme le plus approprié selon le contenu (flowchart, sequence, class, etc.).
            Titre de la section: $sectionTitle
            
            Contenu:
            $sectionContent
            
            Renvoie uniquement le code Mermaid sans aucune explication ni préambule, pas de triple backticks.
        """.trimIndent()

        // 1) Construire un JSONArray pour messages
        val messagesArray = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }

        // 2) Créer le JSON de la requête
        val requestJson = JSONObject().apply {
            put("model", "DeepSeek-V3-0324")
            put("temperature", 0.3)
            put("messages", messagesArray)
        }

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("api-key", API_KEY)              // ou "Authorization", "Bearer $API_KEY"
            .addHeader("Content-Type", "application/json")
            .post(requestJson.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    JSONObject(body)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()
                } else {
                    Log.e(TAG, "API request failed: ${response.code} - $body")
                    "Error: ${response.code}"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in API call", e)
            "Error: ${e.message}"
        }
    }
}
