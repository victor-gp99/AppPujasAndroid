package com.nlink.dadm_u3p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.nlink.dadm_u3p1.databinding.ActivityRegistroBinding
import com.nlink.dadm_u3p1.utils.Tools
import com.nlink.dadm_u3p1.utils.Tools.Companion.snack
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelRegister.setOnClickListener { finish() }

        binding.btnAdd.setOnClickListener {
            var correcto = true
            if(binding.editNewEmail.text.isEmpty()) {
                binding.editNewEmail.error = "Este campo es obligatorio"
                correcto = false;
            }
            if(binding.editNewPass.text.isEmpty()) {
                binding.editNewPass.error = "Este campo es obligatorio"
                correcto = false;
            }
            if(binding.editNewPassRepeat.text.isEmpty()) {
                binding.editNewPassRepeat.error = "Este campo es obligatorio"
                correcto = false;
            }
            if( ! binding.editNewPassRepeat.text.toString().equals(binding.editNewPass.text.toString())) {
                binding.editNewPassRepeat.error = "Las contraseñas no coinciden"
                correcto = false;
            }
            if(binding.editNewName.text.isEmpty()) {
                binding.editNewName.error = "Este campo es obligatorio"
                correcto = false;
            }
            if(correcto) {
                requestAgrega()
            }
        }
    }

    private fun requestAgrega() {
        val params = HashMap<String, String>()
        params.put("usr", binding.editNewEmail.text.toString())
        params.put("pass", binding.editNewPass.text.toString())
        params.put("name", binding.editNewName.text.toString())

        try {
            val url = Tools.wsRegistro

            /*val request = object: StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    println("Respuesta: $response")
                    interpreta(response)
                },
                Response.ErrorListener { error ->
                    Log.e("Login", "Error al hacer login: $error")
                    "Error al registrar, intente mas tarde".snack(binding.btnCancelRegister)
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)*/

            object : Tools() {
                override fun interpreta(stringJSON: String) {
                    procesa(stringJSON)
                }

                override fun errores(error: String) { "Error al registrar, intente mas tarde".snack(binding.btnCancelRegister) }
            }.consumePost(this,url,params)

        } catch (e: Exception) {
            e.printStackTrace()
            "Error con el servidor".snack(binding.btnCancelRegister)
        }
    }

    private fun procesa(strJSON: String) {
        try {
            val json = JSONObject(strJSON)
            if(json.getInt("code") == 200) {
                "Usuario registrado".snack(binding.btnAdd)
                finish()
            } else {
                "No se pudo registrar el usuario".snack(binding.btnCancelRegister)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error al registrar en el servidor, intente mas tarde".snack(binding.btnCancelRegister)
        }
    }

}