package com.nlink.dadm_u3p1.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull
import kotlin.jvm.Throws

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.let {
            it.execSQL("""
                CREATE TABLE sesion(
                    id INTEGER PRIMARY KEY,
                    email TEXT NOT NULL,
                    contrasenia TEXT NOT NULL,
                    nombre TEXT NOT NULL,
                    telefono TEXT,
                    datos_extras TEXT
                )
            """.trimIndent())
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    @Throws
    fun iniciaSession(usuario: Usuario) {
        val db = writableDatabase

        var sql = "INSERT INTO sesion VALUES(?,?,?,?)"
        if(usuario.telefono != null && usuario.datosExtra != null) {
            sql = "INSERT INTO sesion VALUES(?,?,?,?,?,?)"
        }

        val stm = db.compileStatement(sql)

        stm.bindLong(1, usuario.id.toLong())
        stm.bindString(2, usuario.eMail)
        stm.bindString(3, usuario.contrasenia)
        stm.bindString(4, usuario.nombre)

        if(usuario.telefono != null && usuario.datosExtra != null) {
            stm.bindString(5, usuario.telefono)
            stm.bindString(6, usuario.datosExtra)
        }
        stm.execute()

        println("Se guardo la sesion del usuario: ${usuario.id}")

        db.close()
    }

    @Throws
    fun obtieneSesion() : Usuario? {
        val db = readableDatabase

        var usuario: Usuario? = null

        val cursor = db.rawQuery("SELECT * FROM sesion", null)
        if(cursor.moveToNext()) {
            usuario = Usuario(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getStringOrNull(4),
                cursor.getStringOrNull(5)
            )
        }

        db.close()

        return usuario
    }

    @Throws
    fun borraSesion() {
        val db = writableDatabase
        db.execSQL("DELETE FROM sesion")
        db.close()
    }

}