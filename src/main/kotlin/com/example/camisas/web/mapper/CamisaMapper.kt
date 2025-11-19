package com.example.camisas.web.mapper

import com.example.camisas.domain.Camisa
import com.example.camisas.domain.TipoCamisa
import com.example.camisas.web.dto.CamisaRequest
import com.example.camisas.web.dto.CamisaResponse
import com.example.camisas.web.dto.TipoCamisaResponse

object CamisaMapper {
    fun toEntity(req: CamisaRequest, tipo: TipoCamisa) = Camisa(
        nombre = req.nombre,
        talla = req.talla,
        color = req.color,
        precio = req.precio,
        imagenUrl = req.imagenUrl,
        lat = req.lat,
        lng = req.lng,
        tipo = tipo
    )

    fun toResponse(c: Camisa) = CamisaResponse(
        id = c.id!!,
        nombre = c.nombre,
        talla = c.talla,
        color = c.color,
        precio = c.precio,
        imagenUrl = c.imagenUrl,
        lat = c.lat,
        lng = c.lng,
        tipo = TipoCamisaResponse(
            id = c.tipo.id!!,
            nombre = c.tipo.nombre,
            descripcion = c.tipo.descripcion
        )
    )

    fun merge(entity: Camisa, req: CamisaRequest, tipo: TipoCamisa) = entity.copy(
        nombre = req.nombre,
        talla = req.talla,
        color = req.color,
        precio = req.precio,
        imagenUrl = req.imagenUrl,
        lat = req.lat,
        lng = req.lng,
        tipo = tipo
    )
}