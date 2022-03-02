package com.nlink.dadm_u3p1.models

import java.io.Serializable

data class Subasta(
    val id: Int,
    val producto: String,
    val descripcion: String,
    val foto: String,
    val categoria: String,
    val precioInicial: Double,
    val precioFinal: Double?,
    val vence: String,
    val idUsuario: Int?
) : Serializable