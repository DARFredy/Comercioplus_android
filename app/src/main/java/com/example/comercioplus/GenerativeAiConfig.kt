package com.example.comercioplus

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.generationConfig

object GenerativeAiConfig {
    // API Key de Fredy
    private const val API_KEY = "AIzaSyBLmEpy6mqzxKl-F5VXeh7NQbYxWz2znrw" 

    private val config = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 1024
    }

    // FORZAMOS v1 (Producción) en lugar de v1beta
    private val options = RequestOptions(apiVersion = "v1")

    val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = config,
        requestOptions = options
    )
}
