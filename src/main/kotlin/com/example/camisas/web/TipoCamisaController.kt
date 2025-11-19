package com.example.camisas.web

import com.example.camisas.service.TipoCamisaService
import com.example.camisas.web.dto.TipoCamisaRequest
import com.example.camisas.web.dto.TipoCamisaResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/tipos")
class TipoCamisaController(
    private val service: TipoCamisaService
) {
    @GetMapping
    fun list(): List<TipoCamisaResponse> =
        service.list().map { TipoCamisaResponse(it.id!!, it.nombre, it.descripcion) }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): TipoCamisaResponse =
        service.get(id).let { TipoCamisaResponse(it.id!!, it.nombre, it.descripcion) }

    @PostMapping
    fun create(@RequestBody req: TipoCamisaRequest): ResponseEntity<TipoCamisaResponse> {
        val saved = service.create(req)
        val body = TipoCamisaResponse(saved.id!!, saved.nombre, saved.descripcion)
        return ResponseEntity.created(URI.create("/api/tipos/${saved.id}")).body(body)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: TipoCamisaRequest): TipoCamisaResponse {
        val updated = service.update(id, req)
        return TipoCamisaResponse(updated.id!!, updated.nombre, updated.descripcion)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}