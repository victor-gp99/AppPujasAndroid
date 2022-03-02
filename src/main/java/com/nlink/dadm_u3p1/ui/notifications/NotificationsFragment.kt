package com.nlink.dadm_u3p1.ui.notifications

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nlink.dadm_u3p1.R
import com.nlink.dadm_u3p1.databinding.FragmentNotificationsBinding
import com.nlink.dadm_u3p1.models.DBManager
import com.nlink.dadm_u3p1.utils.Tools

class NotificationsFragment : Fragment() {

    //private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var db: DBManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       /* notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)*/
        /*notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
           textView.text = it
       })*/
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        db = DBManager(requireActivity(), Tools.dbName, null, Tools.dbVersion)
        val user = db.obtieneSesion()

        user?.let {
            binding.userView.text = "Usuario: \n"+it.eMail
            binding.nombreView.text ="Nombre: \n" + it.nombre

            if (!it.telefono.equals("null"))
                binding.numView.text ="Numero telefonico: \n"+ it.telefono
            else
                binding.numView.text = "Numero telefonico: \n Sin numero registrado"

            if (!it.datosExtra.equals("null"))
                binding.extrasView.text ="Datos extras: \n "+ it.datosExtra
            else
                binding.extrasView.text = " Datos extras: \n Sin datos extras registrados"


            binding.ntnEditLog.setOnClickListener {
                val intent = Intent (requireActivity(), EditActivity::class.java)
                intent.putExtra("id", user.id)
                startActivity(intent)
            }
        }




        val root: View = binding.root

        return root
    }

    private fun procesa(stringJSON: String) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}