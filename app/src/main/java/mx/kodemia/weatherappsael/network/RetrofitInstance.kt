package mx.kodemia.weatherappsael.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

        fun getRetrofit(): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    }

    /*
    Clase para interceptar el token al momento de hacer login
    de esta forma evitamos poner en cada peticion a la API el Header "Authorization: Bearer"
    con el token necesario para tener la autorizacion.
    Es como obtener el token general para todas las peticiones.
    */
   /* class AuthInterceptor(context: Context) : Interceptor {
        //private val shared = SharedPreferencesInstance.obtenerInstancia(context)

        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder = chain.request().newBuilder()

            *//*
            shared.obtenerSesion()?.let {
                requestBuilder.addHeader("Authorization", "Bearer ${it.token}")
            }
            *//*

            return chain.proceed(requestBuilder.build())
        }
    }*/
