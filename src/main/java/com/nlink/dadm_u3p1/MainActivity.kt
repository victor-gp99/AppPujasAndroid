package com.nlink.dadm_u3p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.nlink.dadm_u3p1.databinding.ActivityMainBinding
import com.nlink.dadm_u3p1.models.DBManager
import com.nlink.dadm_u3p1.models.Usuario
import com.nlink.dadm_u3p1.utils.Tools
import com.nlink.dadm_u3p1.utils.Tools.Companion.toast
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBManager(this, Tools.dbName, null, Tools.dbVersion)

        // Se busca la sesion activa en la Base de Datos local
        haySesionActiva()?.let {
            continuaPrincipal(it)
        }

        binding.btnLogin.setOnClickListener {
            val url = "${Tools.wsAcceso}?usr=${binding.editUsr.text}&pass=${binding.editPass.text}"

            object : Tools() {
                override fun interpreta(stringJSON: String) { procesa(stringJSON) }
                override fun errores(error: String) { Snackbar.make(binding.btnLogin,"Error al acceder",Snackbar.LENGTH_LONG).show() }
            }.consumeGet(this, url)

            /*val request = StringRequest(
                Request.Method.GET, url,
                Response.Listener { response ->
                    println("Respuesta: $response")
                    interpreta(response)
                },
                Response.ErrorListener { error ->
                    Log.e("Login", "Error al hacer login: $error")
                    Snackbar.make(binding.btnLogin,"Error al acceder",Snackbar.LENGTH_LONG).show()
                }
            )
            Volley.newRequestQueue(this).add(request)*/
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun procesa(strJSON: String) {
        try {
            val json = JSONObject(strJSON)
            if(json.getInt("code") == 200 && json.has("resultados") && json.getJSONArray("resultados").length() > 0) {
                val resultados = json.getJSONArray("resultados")
                val jsonUsr = resultados.getJSONObject(0)

               "Bienvenido ${jsonUsr.getString("nombre")}".toast(this)

                val usuario = Usuario(
                    jsonUsr.getInt("id"),
                    jsonUsr.getString("email"),
                    jsonUsr.getString("contrasenia"),
                    jsonUsr.getString("nombre"),
                    jsonUsr.getString("telefono"),
                    jsonUsr.getString("datos_extras")
                )
                //da paso a la app
                continuaPrincipal(usuario)

                //registra el usuario en la base de datos local del telefono
                registraSesion(usuario)

            } else {
                Snackbar.make(binding.btnLogin,"Usuario y/o contraseña incorrectos",Snackbar.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(binding.btnLogin,"Error con el servidor",Snackbar.LENGTH_LONG).show()
        }
    }

    private fun continuaPrincipal(usuario: Usuario) {
        val intent = Intent(this, SubastasActivity::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
        finish()
    }

    private fun registraSesion(usuario: Usuario) {
        try {
            db.iniciaSession(usuario)
        } catch (e: Exception) {
            Log.w("SaveSesion", "Error al guardar la sesion.\n$e")
        }
    }

    private fun haySesionActiva() : Usuario? {
        try {
            return db.obtieneSesion()
        } catch (e: Exception) {
            Log.w("LoadSesion", "Error al buscar la sesion.\n$e")
            return null
        }
    }

}