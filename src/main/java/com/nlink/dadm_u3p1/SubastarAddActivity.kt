package com.nlink.dadm_u3p1

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.core.graphics.toColor
import com.nlink.dadm_u3p1.databinding.ActivitySubastarAddBinding
import com.nlink.dadm_u3p1.utils.DatePickerFragment
import com.nlink.dadm_u3p1.utils.Tools
import com.nlink.dadm_u3p1.utils.Tools.Companion.snack
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class SubastarAddActivity : AppCompatActivity() {

    private  lateinit var binding: ActivitySubastarAddBinding
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =   ActivitySubastarAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "Añade tu subasta"

        binding.editDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.fabUploadImg.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            startActivityForResult(chooserIntent, 1)
        }

        binding.btnAddSubastar.setOnClickListener {
            var correcto = true

            if(binding.editProducto.text.isEmpty()) {
                binding.editProducto.error = "Este campo es obligatorio"
                correcto = false
            }
            if(binding.editDes.text.isEmpty()) {
                binding.editDes.error = "Este campo es obligatorio"
                correcto = false
            }
            if(binding.editCat.text.isEmpty()) {
                binding.editCat.error = "Este campo es obligatorio"
                correcto = false
            }
            if(binding.editPrecio.text.isEmpty()) {
                binding.editPrecio.error = "Este campo es obligatorio"
                correcto = false
            }
            if(binding.editDate.text.isEmpty()) {
                binding.editDate.error = "Este campo es obligatorio"
                correcto = false
            }

            if (binding.UpLoadImg.drawable == null){
                binding.fabUploadImg.backgroundTintList = ColorStateList.valueOf (Color.RED)
                Toast.makeText(this, "Debes subir una imagen", Toast.LENGTH_LONG).show()
                correcto = false
            }

            if(correcto) {
                requestAgrega()
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            data?.let {
                // Actualizar imageView
                try {
                    val fileUri = it.data
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                    binding.UpLoadImg.setImageBitmap(bitmap)
                } catch (e: IOException){
                    e.printStackTrace()
                }

            }
        }
    }

    private fun getStringImg(bmp: Bitmap?): String {
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun requestAgrega() {
        val params = HashMap<String, String>()
        params.put("prod", binding.editProducto.text.toString())
        params.put("desc", binding.editDes.text.toString())
        val strImg = getStringImg(bitmap)
        params.put("foto",strImg)
        params.put("cat", binding.editCat.text.toString())
        params.put("price", binding.editPrecio.text.toString())
        params.put("date", binding.editDate.text.toString())
        try {
            val url = Tools.wsRegistroSubasta
            object : Tools() {

                override fun interpreta(stringJSON: String) {
                    procesa(stringJSON)
                }
                override fun errores(error: String) {

                    "Error al subastar, intente mas tarde".snack(binding.btnCancel)
                }

            }.consumePost(this,url,params)

        } catch (e: Exception) {
            e.printStackTrace()
            "Error con el servidor".snack(binding.btnAddSubastar)
        }
    }

    private fun procesa(stringJSON: String) {
        try {
            val json = JSONObject(stringJSON)
            if(json.getInt("code") == 200) {
                "Subasta añadida".snack(binding.btnAddSubastar)
                finish()
            } else {
                "No se pudo subastar el producto".snack(binding.btnCancel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error al registrar en el servidor, intente mas tarde".snack(binding.btnCancel)
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {

        val mes = month+1
        var date ="$year-$mes-$day"

        if (day < 10)
            date = "$year-$mes-0$day"

        if (mes < 10)
            date = "$year-0$mes-$day"

        if (day < 10 && mes < 10)
            date = "$year-0$mes-0$day"

        binding.editDate.setText(date)
    }
}