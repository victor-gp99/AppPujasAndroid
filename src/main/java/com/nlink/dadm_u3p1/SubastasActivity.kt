package com.nlink.dadm_u3p1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nlink.dadm_u3p1.databinding.ActivitySubastasBinding
import com.nlink.dadm_u3p1.models.DBManager
import com.nlink.dadm_u3p1.utils.Tools

class SubastasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubastasBinding
    private lateinit var db: DBManager

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubastasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBManager(this, Tools.dbName, null, Tools.dbVersion)

        // Boton de regreso
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_subastas)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.submenuCerrar) {
            cierraSesion()
        } else if(item.itemId == R.id.submenuSalir) {
            finish()
        } else if(item.itemId == R.id.agregarSubasta){
              val intent = Intent(this, SubastarAddActivity::class.java)
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)
    }

    private fun cierraSesion() {
        try {
            db.borraSesion()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.w("Cierre","Error al cerrar sesion\n$e")
        }
    }

}