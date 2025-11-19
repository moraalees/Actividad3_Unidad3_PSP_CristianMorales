package com.example.camisas.web

import com.example.camisas.service.CamisaService
import com.example.camisas.web.dto.CamisaRequest
import com.example.camisas.web.dto.CamisaResponse
import com.example.camisas.web.mapper.CamisaMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/camisas")
class CamisaController(
    private val service: CamisaService
) {
    @GetMapping
    fun list(): List<CamisaResponse> =
        service.list().map(CamisaMapper::toResponse)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): CamisaResponse =
        CamisaMapper.toResponse(service.get(id))

    @PostMapping
    fun create(@RequestBody req: CamisaRequest): ResponseEntity<CamisaResponse> {
        val saved = service.create(req)
        return ResponseEntity.created(URI.create("/api/camisas/${saved.id}"))
            .body(CamisaMapper.toResponse(saved))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: CamisaRequest): CamisaResponse =
        CamisaMapper.toResponse(service.update(id, req))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}