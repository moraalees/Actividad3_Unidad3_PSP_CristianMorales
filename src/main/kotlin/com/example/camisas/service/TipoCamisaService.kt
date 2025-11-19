package com.example.camisas.service

import com.example.camisas.domain.TipoCamisa
import com.example.camisas.repository.TipoCamisaRepository
import com.example.camisas.web.dto.TipoCamisaRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TipoCamisaService(
    private val repo: TipoCamisaRepository
) {
    @Transactional(readOnly = true)
    fun list(): List<TipoCamisa> = repo.findAll().sortedBy { it.id }

    @Transactional(readOnly = true)
    fun get(id: Long): TipoCamisa =
        repo.findById(id).orElseThrow { NotFoundException("Tipo id=$id no encontrado") }

    @Transactional
    fun create(req: TipoCamisaRequest): TipoCamisa {
        if (repo.existsByNombreIgnoreCase(req.nombre))
            throw IllegalArgumentException("Ya existe un tipo con nombre '${req.nombre}'")
        return repo.save(TipoCamisa(nombre = req.nombre.trim(), descripcion = req.descripcion))
    }

    @Transactional
    fun update(id: Long, req: TipoCamisaRequest): TipoCamisa {
        val current = get(id)
        return repo.save(current.copy(nombre = req.nombre.trim(), descripcion = req.descripcion))
    }

    @Transactional
    fun delete(id: Long) {
        if (!repo.existsById(id)) throw NotFoundException("Tipo id=$id no encontrado")
        repo.deleteById(id)
    }
}