package com.nlink.dadm_u3p1.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar

abstract class Tools {

    companion object {
        val ws = "http://192.168.100.196:8080/Proyecto3WS"
        val wsAcceso = "$ws/acceso.php"
        val wsRegistro = "$ws/agregar.php"
        val wsSubastas = "$ws/subastas.php"
        val wsPujar = "$ws/pujar.php"
        val wsComprar = "$ws/comprar.php"
        val wsRegistroSubasta = "$ws/agregarSubasta.php"
        val wsEditarUser = "$ws/editarUser.php"
        
        val imgPath = "$ws/imagenes/"

        val dbName = "Subastienda"
        val dbVersion = 1

        fun String.snack(view: View) {
            Snackbar.make(view, this, Snackbar.LENGTH_LONG).show()
        }

        fun String.toast(context: Context) {
            Toast.makeText(context, this, Toast.LENGTH_LONG).show()
        }

    }

    fun consumeGet(context: Context, url: String) {
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Respuesta: $response")
                interpreta(response)
            },
            { error ->
                Log.e("Get", "Error al consumir Get: $error")
                errores("$error")
            }
        )
        Volley.newRequestQueue(context).add(request)
    }

    fun consumePost(context: Context, url: String, params: HashMap<String,String>) {
        val request = object: StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                println("Respuesta: $response")
                interpreta(response)
            },
            Response.ErrorListener { error ->
                Log.e("Post", "Error al consumir Post: $error")
                errores("$error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        Volley.newRequestQueue(context).add(request)
    }

    abstract fun interpreta(stringJSON : String)

    abstract fun errores(error: String)

}