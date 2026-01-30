package com.example.errenteriaapp.ai

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface ChatApi {
    @POST("chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body body: ChatRequest
    ): ChatResponse
}
