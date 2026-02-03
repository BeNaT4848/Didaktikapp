package com.example.errenteriaapp.ai

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Chat-API-arekin komunikatzeko Retrofit interfazea.
 * OpenAI-ren APIaren antzeko interfaze bat inplementatzen du.
 *
 * @since 1.0
 * @see [retrofit2.Retrofit]
 * @see [ChatRequest]
 * @see [ChatResponse]
 */
// Chat-API-arekin komunikatzeko Retrofit interfazea
internal interface ChatApi {

    /**
     * Chat-osaketak eskuratzeko POST eskaera bidaltzen du.
     * OpenAI-ren chat completion endpoint-a erabiltzen du.
     *
     * @param authorization API giltza "Bearer" aurrizkiarekin
     * @param body Chat-eskaeraren gorputza (modeloa, mezua, parametroak)
     * @return [ChatResponse] Chat-erantzuna bektore formarekin
     *
     * @throws [retrofit2.HttpException] HTTP erroreak gertatuz gero
     * @throws [java.io.IOException] Sareko erroreak gertatuz gero
     *
     * @see [ChatRequest]
     * @see [ChatResponse]
     */
    // Chat-osaketak eskuratzeko POST eskaera
    @POST("chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body body: ChatRequest
    ): ChatResponse
}