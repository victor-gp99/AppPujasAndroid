package com.nlink.dadm_u3p1.ui.home

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
import com.nlink.dadm_u3p1.adapters.SubastasAdapter
import com.nlink.dadm_u3p1.databinding.FragmentHomeBinding
import com.nlink.dadm_u3p1.models.Subasta
import com.nlink.dadm_u3p1.utils.Tools
import org.json.JSONObject
import java.lang.Exception

class HomeFragment : Fragment() {

    //private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        /*homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        actualiza()

        binding.fabActualiza.setOnClickListener { actualiza() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("SetTextI18n")
    private fun actualiza() {
        try {
            object : Tools() {
                override fun interpreta(stringJSON: String) {
                    procesa(stringJSON)
                }


                override fun errores(error: String) {
                    Log.e("Subastas", "Error al cargar subastas: $error")
                    binding.textHome.text = "Error al buscar subastas, intente mas tarde"
                    binding.textHome.setTextColor(Color.WHITE)
                    binding.textHome.setBackgroundColor(Color.RED)
                }
            }.consumeGet(binding.root.context, Tools.wsSubastas)
        } catch (e: Exception) {
            Log.e("Subastas", "Error en las subastas: $e")
            binding.textHome.text = "Error al cargar subastas, intente mas tarde"
            binding.textHome.setTextColor(Color.WHITE)
            binding.textHome.setBackgroundColor(Color.RED)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun procesa(stringJSON: String) {
        try {
            val json = JSONObject(stringJSON)

            if(json.getInt("code") == 200 && json.has("resultados") && json.getJSONArray("resultados").length() > 0) {
                val jsonSubastas = json.getJSONArray("resultados")

                val categorias = ArrayList<String>()
                categorias.add("Todas las categorias")

                val subastas = ArrayList<Subasta>()
                for(i in 0..jsonSubastas.length()-1) {
                    val jSubasta = jsonSubastas.getJSONObject(i)

                    val subasta = Subasta(
                        jSubasta.getInt("id"),
                        jSubasta.getString("producto"),
                        jSubasta.getString("descripcion"),
                        jSubasta.getString("foto"),
                        jSubasta.getString("categoria"),
                        jSubasta.getDouble("precio_inicial"),
                        if(!jSubasta.getString("precio_final").equals("null")) jSubasta.getDouble("precio_final") else null,
                        jSubasta.getString("vence"),
                        if(!jSubasta.getString("id_usuario").equals("null")) jSubasta.getInt("id_usuario") else null
                    )

                    if(!categorias.contains(subasta.categoria.uppercase())) {
                        categorias.add(subasta.categoria.uppercase())
                    }

                    println("Subasta:\nId: ${subasta.id}\nProductos: ${subasta.producto}")

                    subastas.add(subasta)
                }

                binding.spinnerCategorias.adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1, categorias)

                binding.textHome.text = "Total de subastas: ${subastas.size}"
                binding.textHome.setTextColor(Color.BLACK)
                binding.textHome.setBackgroundColor(Color.CYAN)

                binding.recyclerSubastas.layoutManager = GridLayoutManager(binding.root.context, 2)
                binding.recyclerSubastas.adapter = SubastasAdapter(
                    binding.root.context,
                    R.layout.item_subasta,
                    subastas,
                )
            } else {
                binding.textHome.text = "En este momento no hay subastas"
                binding.textHome.setTextColor(Color.BLACK)
                binding.textHome.setBackgroundColor(Color.YELLOW)
            }
        } catch (e: Exception) {
            Log.e("Subastas", "Error al procesar subastas: $e")
            binding.textHome.text = "Error en las subastas, intente mas tarde"
            binding.textHome.setTextColor(Color.WHITE)
            binding.textHome.setBackgroundColor(Color.RED)
        }
    }

}