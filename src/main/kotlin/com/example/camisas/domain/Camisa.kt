package com.example.camisas.domain

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "camisas")
data class Camisa(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 120)
    val nombre: String,

    val talla: String? = null,
    val color: String? = null,

    val precio: BigDecimal? = null,

    @Column(name = "imagen_url")
    val imagenUrl: String? = null,

    val lat: Double? = null,
    val lng: Double? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_id")
    val tipo: TipoCamisa
)