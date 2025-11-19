package com.example.camisas.repository

import com.example.camisas.domain.TipoCamisa
import org.springframework.data.jpa.repository.JpaRepository

interface TipoCamisaRepository : JpaRepository<TipoCamisa, Long> {
    fun existsByNombreIgnoreCase(nombre: String): Boolean
}