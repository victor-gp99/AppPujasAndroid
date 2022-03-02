package com.nlink.dadm_u3p1.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nlink.dadm_u3p1.PujarActivity
import com.nlink.dadm_u3p1.R
import com.nlink.dadm_u3p1.models.Subasta
import com.nlink.dadm_u3p1.utils.Tools
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ComprasAdapter(
    val ctx: Context,
    val layout: Int,
    val subastas: ArrayList<Subasta>
) : RecyclerView.Adapter<ComprasAdapter.SubastasVH>() {

    inner class SubastasVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(s: Subasta) {
            val img = itemView.findViewById<ImageView>(R.id.imgItemSubasta)
            val textProducto = itemView.findViewById<TextView>(R.id.textItemProducto)
            val textCategoria = itemView.findViewById<TextView>(R.id.textItemCategoria)
            val textPrecio = itemView.findViewById<TextView>(R.id.textItemPrecio)
            val textVence = itemView.findViewById<TextView>(R.id.textItemVence)
            val btnSubastar = itemView.findViewById<TextView>(R.id.btnItemSubastar)

            Picasso.get().load("${Tools.imgPath + s.foto}").into(img)

            textProducto.text = s.producto
            textCategoria.text = s.categoria
            textPrecio.text = if(s.precioFinal != null) "${s.precioFinal}" else "${s.precioInicial}"
            textVence.text = s.vence

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val simpleFormat = DateTimeFormatter.ISO_DATE
                val fechaVencimiento = LocalDate.parse(s.vence, simpleFormat)

                if(LocalDate.now().isAfter(fechaVencimiento) ) {
                    println("Subasta vencida")
                    btnSubastar.text = "SUBASTA VENCIDA"
                    btnSubastar.isEnabled = false

                } else {
                    btnSubastar.setBackgroundColor(Color.GREEN)
                    btnSubastar.text = "GANANDO SUBASTA"
                }

            } else {
                println("No compatible")
                btnSubastar.text = "NO DISPONIBLE"
                btnSubastar.isEnabled = false
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubastasVH {
        val view = LayoutInflater.from(ctx).inflate(layout, null)
        return SubastasVH(view)
    }

    override fun onBindViewHolder(holder: SubastasVH, position: Int) {
        holder.bind(subastas[position])
    }

    override fun getItemCount(): Int {
        return subastas.size
    }

}