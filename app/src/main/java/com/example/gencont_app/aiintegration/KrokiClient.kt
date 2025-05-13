import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object KrokiClient {
    private val client = OkHttpClient()
    private val JSON = "text/plain".toMediaType()

    /**
     * Envoie le code Mermaid à kroki.io et renvoie le binaire PNG.
     */
    fun renderMermaidToPng(mermaidCode: String): ByteArray {
        val url = "https://kroki.io/mermaid/png"
        // POST en text/plain : le corps est juste le texte du diagramme
        val body = mermaidCode.toRequestBody(JSON)
        val req = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw IOException("Kroki render failed: HTTP ${resp.code}")
            }
            return resp.body!!.bytes()
        }
    }
}
