package com.example.camisas.web.dto

import java.math.BigDecimal

data class CamisaResponse(
    val id: Long,
    val nombre: String,
    val talla: String?,
    val color: String?,
    val precio: BigDecimal?,
    val imagenUrl: String?,
    val lat: Double?,
    val lng: Double?,
    val tipo: TipoCamisaResponse
)