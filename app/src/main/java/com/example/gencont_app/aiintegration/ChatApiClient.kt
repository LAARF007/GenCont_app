package com.example.gencont_app.api

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object ChatApiClient {
    private const val TAG = "ChatAPI"
    private const val ENDPOINT = "https://models.inference.ai.azure.com/chat/completions"
    private const val API_KEY = "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"  // Remplacez par votre clé
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    /**
     * @param onResult callback déclenché avec le JSON de cours généré
     */
    fun generateCourseJson(
        titre: String,
        niveau: String,
        language: String,
        description: String,
        emotion: String,
        onResult: (jsonCourse: String, isSuccess: Boolean) -> Unit
    ) {
        val systemPrompt = """
    Tu es un assistant pédagogique expert dans la génération de cours structurés au format JSON.
    Tu reçois en user prompt :
      • un titre de cours
      • un niveau parmi {débutant, intermédiaire, avancé}
      • la language de contenu 
      • une courte description
      • une émotion parmi {happy, neutral, sad, excited, anxious}

    Selon l’`emotion` :
      – Adapte la **tonalité** :
          • happy/excited → chaleureux, encourageant, dynamique  
          • neutral       → clair, factuel, équilibré  
          • sad/anxious   → empathique, rassurant, apaisant  
      – Détermine le **nombre de sections** :
          • happy/excited → 4–6 sections courtes, rythmées  
          • neutral       → 3–4 sections de longueur standard  
          • sad/anxious   → 2–3 sections concises, calmes  
      – Ajuste le **degré de détail** :
          • débutant      → explications simples, exemples basiques  
          • intermédiaire → explications détaillées, exemples concrets  
          • avancé        → approfondissements, cas d’usage complexes  

    Chaque section doit contenir :
      1. **titre**  
      2. **contenu** explicatif adapté au niveau et à l’émotion  
      3. **exemple** concret  
      4. **quiz** de 4 questions, chacune avec 3 réponses (`lib`) dont 1 seule a `"isCorrect": true`.

   format de ce JSON :
    {
      "cours": {
        "titre": "...",
        "niveau": "...",
        "nombreSection": N,
        "sections": [
          {
            "titre": "...",
            "contenu": "...",
            "exemple": "...",
            "quiz": [
              {
                "libelle": "Question 1 …",
                "reponses": [
                  { "lib": "Réponse A", "isCorrect": false },
                  { "lib": "Réponse B", "isCorrect": true  },
                  { "lib": "Réponse C", "isCorrect": false }
                ]
              },
              { "libelle": "Question 2 …", "reponses": [ { … } ] },
              { "libelle": "Question 3 …", "reponses": [ { … } ] },
              { "libelle": "Question 4 …", "reponses": [ { … } ] }
            ]
          }
          // … autres sections
        ]
      }
    }
""".trimIndent()


        val userPayload = """
        {
          "titre": "${titre.replace("\"","\\\"")}",
          "niveau": "${niveau.replace("\"","\\\"")}",
          "language": "${language.replace("\"","\\\"")}",
          "description": "${description.replace("\"","\\\"")}",
          "emotion": "${emotion.replace("\"","\\\"")}"
        }
        Instruction :
        Génère un JSON unique avec ce schéma :
        {
          "cours": { … }
        }
        """.trimIndent()


        // Dans generateCourseJson(), remplacez la construction du payload par :
        val messagesArray = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", userPayload)
            })
        }
        // Construction du JSON complet
        val fullJson = JSONObject().apply {
            put("model", "DeepSeek-V3-0324")
            put("temperature", 1.0)
            put("top_p", 1.0)
            put("max_tokens", 6000)
            put("messages", messagesArray)    // <— utilise maintenant un JSONArray
        }.toString(2)

        Log.d(TAG, "Payload envoyé :\n$fullJson")

        val requestBody = fullJson
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("Authorization", "Bearer $API_KEY")
            .post(requestBody)
            .build()

  /*      client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Erreur réseau : ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    // ② Affiche le code ET tout le corps d'erreur
                    Log.e(TAG, "HTTP ${response.code} - body:\n$bodyString")
                    return
                }

                try {
                    val content = JSONObject(bodyString)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    Log.d(TAG, "Cours JSON généré :\n$content")
                } catch (e: Exception) {
                    Log.e(TAG, "Parsing échoué : ${e.message}", e)
                    Log.d(TAG, "Réponse brute :\n$bodyString")
                }
            }
        })*/

        // … construction du fullJson, envoi de la requête …
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Erreur réseau : ${e.message}", e)
                onResult("Erreur réseau : ${e.message}", false)

            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e(TAG, "Échec HTTP ${response.code} - body:\n$bodyString")
                    onResult("Erreur HTTP ${response.code}", false)
                    return
                }
                /*try {
                    val content = JSONObject(bodyString)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    // **ici**, on renvoie la chaîne JSON complète de "cours"
                    onResult(content)
                } catch (e: Exception) {
                    Log.e(TAG, "Parsing échoué : ${e.message}", e)
                    Log.d(TAG, "Réponse brute :\n$bodyString")
                }*/
                try {
                    val content = JSONObject(bodyString)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    Log.d(TAG, "Cours JSON généré avec succès.")
                    onResult(content, true)
                } catch (e: Exception) {
                    Log.e(TAG, "Erreur lors du parsing JSON : ${e.message}", e)
                    Log.d(TAG, "Réponse brute :\n$bodyString")
                    onResult("Erreur de parsing : ${e.message}", false)
                }
            }
        })
    }
}