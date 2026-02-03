package com.example.errenteriaapp.ai

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Txat-mezuak bidaltzeko eta jasotzeko biltegia.
 * Groq APIarekin komunikatzeko erabiltzen da Retrofit-en bidez.
 *
 * @property apiKey Groq API giltza
 * @constructor API giltza erabiliz ChatRepository bat sortzen du
 *
 * @throws [IllegalArgumentException] API giltza hutsa bada
 * @see [ChatApi]
 * @see [ChatConfig]
 */
// Txat-mezuak bidaltzeko eta jasotzeko biltegia
class ChatRepository(
    private val apiKey: String
) {
    /**
     * ChatApi instantzia pribatua.
     */
    private val api: ChatApi = createApi()

    /**
     * Txat-mezu zerrenda bidaltzen du APIra eta erantzuna jasotzen du.
     *
     * @param messages Bidali beharreko txat-mezu zerrenda
     * @return APIren erantzunaren edukia edo errore mezua
     *
     * @throws [retrofit2.HttpException] HTTP erroreak gertatuz gero
     * @throws [java.io.IOException] Sareko erroreak gertatuz gero
     * @see [ChatRequest]
     * @see [ChatResponse]
     */
    // Txat-mezu zerrenda bidaltzen du APIra eta erantzuna jasotzen du
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

    /**
     * ChatApi instantzia konfiguratzen eta sortzen du.
     *
     * @return Konfiguratutako ChatApi instantzia
     *
     * @see [OkHttpClient]
     * @see [Retrofit]
     * @see [Moshi]
     */
    // ChatApi instantzia konfiguratzen eta sortzen du
    private fun createApi(): ChatApi {
        // HTTP erregistroa gaitu (BASIC mailan)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        // OkHttpClient konfiguratu denbora-mugekin
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Moshi JSON parser konfiguratu
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // Retrofit instantzia sortu
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ChatApi::class.java)
    }
}