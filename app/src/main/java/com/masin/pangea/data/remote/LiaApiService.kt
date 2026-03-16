package com.masin.pangea.data.remote

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class LiaApiService {

    companion object {
        private const val BASE_URL = "https://masina-chat-ai-793004668.development.catalystserverless.com/server/masina_chat_ai_function"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): LiaApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = MasinaMessageRequest(
                    prompt = message,
                    conversationHistory = conversationHistory.map {
                        ConversationItem(role = it.role, content = it.content)
                    }
                )

                val jsonBody = gson.toJson(requestBody)
                val request = Request.Builder()
                    .url("$BASE_URL/")
                    .post(jsonBody.toRequestBody(JSON_MEDIA_TYPE))
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val apiResponse = gson.fromJson(responseBody, MasinaApiResponse::class.java)
                    val responseText = apiResponse.response?.trim()
                    if (!responseText.isNullOrEmpty()) {
                        LiaApiResponse.Success(
                            message = responseText,
                            timestamp = (apiResponse.createdTime ?: System.currentTimeMillis() / 1000.0).toString()
                        )
                    } else {
                        LiaApiResponse.Error(apiResponse.error ?: "Respuesta vacía del servidor")
                    }
                } else {
                    try {
                        val errorResponse = gson.fromJson(responseBody, MasinaErrorResponse::class.java)
                        LiaApiResponse.Error(errorResponse.error ?: "Error de comunicación: ${response.code}")
                    } catch (e: Exception) {
                        LiaApiResponse.Error("Error de comunicación: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LiaApiResponse.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun checkStatus(): ServerStatus {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/")
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                // MASINA responde 200 en GET / con HTML de bienvenida
                if (response.isSuccessful) ServerStatus.Connected else ServerStatus.Disconnected
            } catch (e: Exception) {
                e.printStackTrace()
                ServerStatus.Disconnected
            }
        }
    }
}

data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class LiaApiResponse {
    data class Success(val message: String, val timestamp: String) : LiaApiResponse()
    data class Error(val error: String) : LiaApiResponse()
}

sealed class ServerStatus {
    object Connected : ServerStatus()
    object Disconnected : ServerStatus()
    object Checking : ServerStatus()
}

/** Request para API MASINA Chat AI (formato QuickML) */
data class MasinaMessageRequest(
    val prompt: String,
    @SerializedName("conversation_history") val conversationHistory: List<ConversationItem> = emptyList()
)

/** Response de API MASINA: { response, usage, model, created_time, total_time } */
data class MasinaApiResponse(
    val response: String?,
    val usage: MasinaUsage? = null,
    val model: String? = null,
    @SerializedName("created_time") val createdTime: Double? = null,
    @SerializedName("total_time") val totalTime: Double? = null,
    val error: String? = null
)

data class MasinaUsage(
    @SerializedName("completion_tokens") val completionTokens: Int? = null,
    @SerializedName("prompt_tokens") val promptTokens: Int? = null,
    @SerializedName("total_tokens") val totalTokens: Int? = null
)

data class MasinaErrorResponse(
    val error: String? = null,
    val message: String? = null
)

data class ConversationItem(
    val role: String,
    val content: String
)
