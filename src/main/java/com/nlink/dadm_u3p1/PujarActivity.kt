package com.nlink.dadm_u3p1

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nlink.dadm_u3p1.databinding.ActivityPujarBinding
import com.nlink.dadm_u3p1.models.DBManager
import com.nlink.dadm_u3p1.models.Subasta
import com.nlink.dadm_u3p1.utils.Tools
import com.nlink.dadm_u3p1.utils.Tools.Companion.snack
import com.nlink.dadm_u3p1.utils.Tools.Companion.toast
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception

class PujarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPujarBinding

    private lateinit var subasta: Subasta

    private lateinit var db: DBManager

    private var precio = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPujarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBManager(this, Tools.dbName, null, Tools.dbVersion)

        subasta = intent.getSerializableExtra("producto") as Subasta

        if(subasta != null) {
            setSupportActionBar(findViewById(R.id.toolbar))

            binding.toolbarLayout.title = subasta.producto

            val imageView = ImageView(this)

            Picasso.get().load("${Tools.imgPath + subasta.foto}").into(imageView)

            binding.toolbarLayout.background = imageView.drawable
            //binding.toolbarLayout.setBackgroundResource(R.mipmap.ic_launcher)

            val textDesc = findViewById<TextView>(R.id.textDescripcion)
            textDesc.textSize = 18.0f
            textDesc.setTextColor(Color.BLACK)

            textDesc.text = subasta.descripcion
            textDesc.append("\n\nSe subasta por: $")
            if(subasta.precioFinal != null) {
                subasta.precioFinal?.let {
                    precio = it
                    textDesc.append("${it}")
                }
            } else {
                precio = subasta.precioInicial
                textDesc.append("${subasta.precioInicial}")
            }

            binding.fabPujar.setOnClickListener { pujar() }
        } else {
            "Subasta incorrecta".toast(this)
            finish()
        }

    }

    private fun pujar() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Pujar")
        dialog.setMessage("¿De cuánto será su subasta?")

        val editText = EditText(this)
        editText.hint = "Valor de subasta"
        editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        dialog.setView(editText)

        dialog.setPositiveButton("Aplicar") { dialogInterface: DialogInterface, i: Int ->
            if (editText.text.isEmpty()) {
                "Valor incorrecto de su subasta".snack(binding.fabPujar)
            } else {
                try {
                    val precioPuja = editText.text.toString().toDouble()

                    if (precioPuja > precio) {
                        // Invocar al WS para guardar la puja/subasta
                        guardarPuja(precioPuja)
                    } else {
                        "Su subasta no pude ser menor al precio actual".snack(binding.fabPujar)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Valor incorrecto de su puja".snack(binding.fabPujar)
                }
            }
        }

        dialog.setNeutralButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun guardarPuja(bid: Double) {
        try {
            val usr = db.obtieneSesion()

            usr?.let {
                val params = HashMap<String,String>()
                params.put("amount", "$bid")
                params.put("usr", "${it.id}")
                params.put("product", "${subasta.id}")

                object : Tools() {
                    override fun interpreta(stringJSON: String) {
                        val json = JSONObject(stringJSON)
                        if(json.getInt("code") == 200) {
                            "Puja realizada correctamente".toast(this@PujarActivity)
                            this@PujarActivity.finish()
                        } else {
                            "No se pudo realizar su puja ${json.getInt("code")} ".snack(binding.fabPujar)
                        }
                    }

                    override fun errores(error: String) {
                        "Error al pujar, intente mas tarde".snack(binding.fabPujar)
                    }
                }.consumePost(this, Tools.wsPujar, params)
            }
        } catch (e: Exception) {
            Log.w("LoadSesion", "Error al buscar la sesion.\n$e")
        }

    }

}