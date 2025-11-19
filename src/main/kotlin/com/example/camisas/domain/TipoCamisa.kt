package com.example.camisas.domain

import jakarta.persistence.*

@Entity
@Table(name = "tipos_camisa")
data class TipoCamisa(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    val nombre: String,

    val descripcion: String? = null
)