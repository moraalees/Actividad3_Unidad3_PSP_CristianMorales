package com.example.camisas.service

import com.example.camisas.domain.Camisa
import com.example.camisas.repository.CamisaRepository
import com.example.camisas.repository.TipoCamisaRepository
import com.example.camisas.web.dto.CamisaRequest
import com.example.camisas.web.mapper.CamisaMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CamisaService(
    private val repo: CamisaRepository,
    private val tipoRepo: TipoCamisaRepository
) {

    @Transactional(readOnly = true)
    fun list(): List<Camisa> = repo.findAll().sortedBy { it.id }

    @Transactional(readOnly = true)
    fun get(id: Long): Camisa =
        repo.findById(id).orElseThrow { NotFoundException("Camisa id=$id no encontrada") }

    @Transactional
    fun create(req: CamisaRequest): Camisa {
        val tipo = tipoRepo.findById(req.tipoId).orElseThrow {
            NotFoundException("Tipo id=${req.tipoId} no encontrado")
        }
        return repo.save(CamisaMapper.toEntity(req, tipo))
    }

    @Transactional
    fun update(id: Long, req: CamisaRequest): Camisa {
        val current = get(id)
        val tipo = tipoRepo.findById(req.tipoId).orElseThrow {
            NotFoundException("Tipo id=${req.tipoId} no encontrado")
        }
        return repo.save(CamisaMapper.merge(current, req, tipo))
    }

    @Transactional
    fun delete(id: Long) {
        if (!repo.existsById(id)) throw NotFoundException("Camisa id=$id no encontrada")
        repo.deleteById(id)
    }
}
