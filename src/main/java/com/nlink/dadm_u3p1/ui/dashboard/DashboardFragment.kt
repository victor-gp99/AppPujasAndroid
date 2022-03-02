package com.nlink.dadm_u3p1.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.nlink.dadm_u3p1.R
import com.nlink.dadm_u3p1.adapters.ComprasAdapter
import com.nlink.dadm_u3p1.databinding.FragmentDashboardBinding
import com.nlink.dadm_u3p1.models.DBManager
import com.nlink.dadm_u3p1.models.Subasta
import com.nlink.dadm_u3p1.utils.Tools
import org.json.JSONObject
import java.lang.Exception

class DashboardFragment : Fragment() {

   // private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private lateinit var db: DBManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dashboardViewModel = ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
        /*dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        db = DBManager(requireActivity(), Tools.dbName, null, Tools.dbVersion)

        actualiza()
         binding.fabActualizaC.setOnClickListener {
             actualiza()
         }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("SetTextI18n")
    private fun actualiza() {
        try {
            val usr = db.obtieneSesion()
            usr?.let {

                val params = HashMap<String,String>()
                params.put("id","${it.id}")

                object : Tools() {
                    override fun interpreta(stringJSON: String) {
                        procesa(stringJSON)
                    }

                    override fun errores(error: String) {
                        Log.e("Compras", "Error al cargar compras: $error")
                        binding.textDash.text = "Error al buscar compras, intente mas tarde"
                        binding.textDash.setTextColor(Color.WHITE)
                        binding.textDash.setBackgroundColor(Color.RED)
                    }
                }.consumePost(binding.root.context, Tools.wsComprar, params)

            }

        } catch (e: Exception) {
            Log.e("Compras", "Error en las Compras: $e")
            binding.textDash.text = "Error al cargar Compras, intente mas tarde"
            binding.textDash.setTextColor(Color.WHITE)
            binding.textDash.setBackgroundColor(Color.RED)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun procesa(stringJSON: String) {
        try {
            val json = JSONObject(stringJSON)
            if(json.getInt("code") == 200 && json.has("resultados") && json.getJSONArray("resultados").length() > 0) {
                val jsonCompras = json.getJSONArray("resultados")
                val compras = ArrayList<Subasta>()//si es necesario hacer model compras
                for(i in 0 until jsonCompras.length()) {
                    val jCompra = jsonCompras.getJSONObject(i)
                    val compra = Subasta(
                        jCompra.getInt("id"),
                        jCompra.getString("producto"),
                        jCompra.getString("descripcion"),
                        jCompra.getString("foto"),
                        jCompra.getString("categoria"),
                        jCompra.getDouble("precio_inicial"),
                        if(!jCompra.getString("precio_final").equals("null")) jCompra.getDouble("precio_final") else null,
                        jCompra.getString("vence"),
                        if(!jCompra.getString("id_usuario").equals("null")) jCompra.getInt("id_usuario") else null
                    )

                    println("Compra:\nId: ${compra.id}\nProductos: ${compra.producto}")

                    compras.add(compra)
                }

                binding.textDash.text = "Total de compras: ${compras.size}"
                binding.textDash.setTextColor(Color.BLACK)
                binding.textDash.setBackgroundColor(Color.CYAN)

                binding.recyclerCompras.layoutManager = GridLayoutManager(binding.root.context, 2)

                binding.recyclerCompras.adapter = ComprasAdapter(
                    binding.root.context,
                    R.layout.item_compra,
                    compras
                )
            } else {
                binding.textDash.text = "En este momento no hay compras"
                binding.textDash.setTextColor(Color.BLACK)
                binding.textDash.setBackgroundColor(Color.YELLOW)
            }
        } catch (e: Exception) {
            Log.e("Compras", "Error al procesar compras: $e")
            binding.textDash.text = "Error en las compras, intente mas tarde"
            binding.textDash.setTextColor(Color.WHITE)
            binding.textDash.setBackgroundColor(Color.RED)
        }
    }



}