package com.nlink.dadm_u3p1.ui.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nlink.dadm_u3p1.R
import com.nlink.dadm_u3p1.databinding.ActivityEditBinding
import com.nlink.dadm_u3p1.utils.Tools
import com.nlink.dadm_u3p1.utils.Tools.Companion.snack
import com.nlink.dadm_u3p1.utils.Tools.Companion.toast
import org.json.JSONObject
import kotlin.math.log

class EditActivity : AppCompatActivity() {
    private  lateinit var binding : ActivityEditBinding
   private var sessionId :Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.title = "Edita los datos de tu perfil"

        try {
            sessionId = intent.getIntExtra("id",0)
        }catch (e:Exception){
            Log.e("putExtraId", "Error al pasar la sesion: "+ e.printStackTrace() )
        }

        binding.btnGuardar.setOnClickListener {
            var correcto = true

            if (binding.editContra.text.isEmpty()) {
                binding.editContra.error = "Este campo es obligatorio"
                correcto = false
            }
            if (binding.editNombre.text.isEmpty()) {
                binding.editNombre.error = "Este campo es obligatorio"
                correcto = false
            }

            if (binding.editTelefono.text.isEmpty()) {
                binding.editTelefono.setText("null")
            }
            if (binding.editDatosExtras.text.isEmpty()) {
                binding.editDatosExtras.setText("null")
            }

            if (correcto) {
                requestEditar()
            }
        }

    }

    private fun requestEditar() {
        val params = HashMap<String, String>()

        if (sessionId != 0)
            params.put("id", sessionId.toString())
        else
            println("sessionId = ${sessionId}")

        params.put("contra", binding.editContra.text.toString())
        params.put("nombre", binding.editNombre.text.toString())
        params.put("telefono", binding.editTelefono.text.toString())
        params.put("datosExtra", binding.editDatosExtras.text.toString())

        try {
            val url = Tools.wsEditarUser
            object : Tools() {

                override fun interpreta(stringJSON: String) {
                    procesa(stringJSON)
                }
                override fun errores(error: String) {
                    "Error al editar, intente mas tarde".snack(binding.btnGuardar)
                }

            }.consumePost(this,url,params)

        } catch (e: Exception) {
            e.printStackTrace()
            "Error con el servidor".snack(binding.btnGuardar)
        }
    }

    private fun procesa(stringJSON: String) {
        try {
            val json = JSONObject(stringJSON)
            if(json.getInt("code") == 200) {
                "Usuario editado exitosamente". toast(this)
                finish()
            } else {
                "No se pudo editar el usuario".snack(binding.btnGuardar)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error al actualizar en el servidor, intente mas tarde".snack(binding.btnGuardar)
    }
}
}