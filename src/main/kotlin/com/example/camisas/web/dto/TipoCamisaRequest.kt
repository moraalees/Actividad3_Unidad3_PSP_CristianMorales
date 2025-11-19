package com.example.camisas.web.dto

import jakarta.validation.constraints.NotBlank

data class TipoCamisaRequest(
    @field:NotBlank
    val nombre: String,
    val descripcion: String? = null
)
