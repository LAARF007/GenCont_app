package com.example.gencont_app.aiintegration


import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

import org.json.JSONObject
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

    fun sendMessage(userContent: String) {
        val jsonPayload = """
            {
                "model": "DeepSeek-V3-0324",
                "messages": [
                    { "role": "system", "content": "Vous êtes un assistant expert en Kotlin/Android." },
                    { "role": "user",   "content": "${userContent.replace("\"","\\\"")}" }
                ],
                "temperature": 1.0,
                "top_p": 1.0,
                "max_tokens": 6000
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonPayload.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Erreur réseau : ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e(TAG, "Requête échouée : HTTP ${it.code}")
                        return
                    }
                    val respString = it.body?.string() ?: "null"

                    // === Nouveau : on parse et on extrait le contenu ===
                    try {
                        val root = JSONObject(respString)
                        val choices = root.getJSONArray("choices")
                        val first = choices.getJSONObject(0)
                        val message = first.getJSONObject("message")
                        val content = message.getString("content")
                        // Affiche uniquement le texte du modèle
                        Log.d(TAG, "Contenu du modèle :\n$content")
                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur de parsing JSON : ${e.message}", e)
                        // En fallback, on log le JSON intégral
                        Log.d(TAG, "Réponse brute : $respString")
                    }
                }
            }
        })
    }
}

