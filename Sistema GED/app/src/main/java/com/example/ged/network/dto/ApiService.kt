package com.example.ged.network

import com.example.ged.network.dto.ConsolidadoRequest
import com.example.ged.network.dto.ConsolidadoResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/consolidados")
    fun criarConsolidado(
        @Body request: ConsolidadoRequest
    ): Call<ConsolidadoResponse>
}