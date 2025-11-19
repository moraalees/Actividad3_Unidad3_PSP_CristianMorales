package com.example.camisas.repository

import com.example.camisas.domain.Camisa
import org.springframework.data.jpa.repository.JpaRepository

interface CamisaRepository : JpaRepository<Camisa, Long>