package com.nlink.dadm_u3p1.models

import java.io.Serializable

data class Usuario(
    val id: Int,
    val eMail: String,
    val contrasenia: String,
    val nombre: String,
    val telefono: String?,
    val datosExtra: String?
) : Serializable