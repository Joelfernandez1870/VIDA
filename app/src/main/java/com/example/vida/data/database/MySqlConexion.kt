package com.example.vida.data.database

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MySqlConexion {

   private const val URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_vida_db"
   private const val USER = "freedb_grupo9"
    private const val PASS = "w"+"$"+"VEVZ9CN*MGR#4"

    //private const val URL = "jdbc:mysql://10.0.2.2:3306/vida"
    //private const val USER = "root"
    //private const val PASS = "root"

    fun getConexion(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            conn = DriverManager.getConnection(URL, USER, PASS)
        }catch (ex: SQLException){
            Log.e("Error sql", ex.message.toString())
        }catch (ex: ClassNotFoundException){
            Log.e("Error class not found", ex.message.toString())
        }
        return conn
    }

}
