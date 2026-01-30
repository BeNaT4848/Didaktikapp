package com.example.errenteriaapp.ai

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ChatRepository(
    private val apiKey: String
) {
    private val api: ChatApi = createApi()

    suspend fun send(messages: List<ChatMessage>): String {
        if (apiKey.isBlank()) {
            return "Falta GROQ_API_KEY en local.properties"
        }
        val response = api.chatCompletions(
            authorization = "Bearer $apiKey",
            body = ChatRequest(
                model = ChatConfig.MODEL,
                messages = messages
            )
        )
        return response.choices.firstOrNull()?.message?.content ?: "(Sin respuesta)"
    }

    private fun createApi(): ChatApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ChatApi::class.java)
    }
}
