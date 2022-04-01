package mx.kodemia.weatherapp.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import mx.kodemia.weatherappsael.model.OneCall

object SharedPreferencesInstance {

    //Se crea la instancia
    val sharedPref = SharedPreferencesInstance

    //Se crea la variable para obtener las preferencias
    lateinit var sharedPreferences: SharedPreferences

    //Se crea el editor
    lateinit var editor: SharedPreferences.Editor

    //Obtenemos la instancia de nuestro objeto
    fun obtenerInstancia(context: Context): SharedPreferencesInstance{
        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        return sharedPref
    }

}