package com.example.ged.network.dto

data class DocumentoRequest(

    val nomeArquivo: String?,

    val linhaDigitavel: String?,

    val valor: Double?,

    val vencimento: String?,

    val beneficiario: String?
)