package mx.kodemia.weatherappsael.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.kodemia.weatherapp.core.SharedPreferencesInstance
import mx.kodemia.weatherapp.core.checkPermissions
import mx.kodemia.weatherapp.core.startLocationPermissionRequest
import mx.kodemia.weatherapp.model.Daily
import mx.kodemia.weatherapp.view.adapters.HoursAdapter
import mx.kodemia.weatherapp.view.adapters.InfoAdapter
import mx.kodemia.weatherappsael.R
import mx.kodemia.weatherappsael.databinding.ActivityMainBinding
import mx.kodemia.weatherappsael.model.*
import mx.kodemia.weatherappsael.network.WeatherService
import mx.kodemia.weatherappsael.utils.checkForInternet
import mx.kodemia.weatherappsael.view.adapter.DaysAdapter
import mx.kodemia.weatherappsael.viewModels.MainActivityViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityError"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val listInfo: MutableList<ReInfo> = mutableListOf()
    private val listInfoSecondView: MutableList<ReInfo> = mutableListOf()
    private val listIncons: MutableList<Int> = mutableListOf()

    private var latitude = ""
    private var longitude = ""

    private var units = false
    private var language = false
    var unit = "metric"
    var languageCode = "es"

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var shared: SharedPreferencesInstance

    val viewModel: MainActivityViewModel by viewModels()

    /**
     * Punto de entrada para el API Fused Location Provider.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!checkPermissions(this)) {
            requestPermissions()
        } else {
            // despues de que se obtiene la location se ejecuta el setUpViewData con esa location
            getLastLocation() { location ->
                mandarDatosWeather(
                    latitude,
                    longitude,
                    unit,
                    languageCode,
                    "37fb2ab875e61b9769e410901358661b"
                )
                mandarDatosCity(latitude, longitude, "37fb2ab875e61b9769e410901358661b")
                observers()
            }
        }
    }
    private fun init(){
        //Shared
        shared = SharedPreferencesInstance.obtenerInstancia(this)

        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onCreate()
    }

    private fun mandarDatosWeather(lat: String, lon: String, units: String?, lang: String?, appid: String) {
        viewModel.getWeather(lat, lon, units, lang, appid)
    }

    private fun mandarDatosCity(lat: String, lon: String, appid: String){
        viewModel.getCity(lat, lon, appid)
    }

    private fun observers(){
        viewModel.getWeatherResponse.observe(this) {weatherEntity: OneCall ->
            lifecycleScope.launch {
                weatherEntity.apply {
                    formatResponse(weatherEntity)
                }
            }

        }

        viewModel.getCityResponse.observe(this){ cityEntity: List<City> ->
            lifecycleScope.launch {
                cityEntity.apply {
                    formatResponseCity(cityEntity)
                }
            }

        }

    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            //Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn´t check the "Don´t ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_retionale, android.R.string.ok)
            {
                //Request permission
                startLocationPermissionRequest(this)
            }
        } else {
            //Request permission. It´s possible this can be auto answered if device policy
            //Si la configuracion del dispositivo define el permiso a un estado prefefinido o
            //si  el usuario anteriormente activo "No preguntar de nuevo"
            Log.i(TAG, "Solicitando permiso")
            startLocationPermissionRequest(this)
        }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content), getString(snackStrId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )

        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocation: (location: Location) -> Unit) {
        Log.d(TAG, "Aqui estoy: $latitude Long: $longitude")
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {
                    val location = taskLocation.result

                    latitude = location?.latitude.toString()
                    longitude = location?.longitude.toString()
                    Log.e(TAG, "GetLastLoc Lat: $latitude Long: $longitude")

                    onLocation(taskLocation.result)
                } else {
                    Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun setupViewData(location: Location) {
        if (checkForInternet(this)) {
            lifecycleScope.launch {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        } else {
            showError("Sin acceso a Internet")
            binding.containerPrimaryMain.isVisible = false
            binding.constraineLayoutContainerWeather.isVisible = false
        }
    }

    private fun initRecycler(recyclerView: RecyclerView, recyclerViewSecondView: RecyclerView, weatherEntity: OneCall){
        val sunrise = weatherEntity.current.sunrise
        val sunriseSecond = weatherEntity.daily[1].sunrise
        val sunriseFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
        val sunriseFormatSecond = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunriseSecond * 1000))

        val sunset = weatherEntity.current.sunset
        val sunsetSecond = weatherEntity.daily[1].sunset
        val sunsetFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
        val sunsetFormatSecond = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunsetSecond * 1000))

        val adaptador = InfoAdapter(this,listInfo,listIncons)
        val adapterSecondView = InfoAdapter(this,listInfoSecondView,listIncons)

        listInfo.add(ReInfo(weatherEntity.current.humidity.toString(),R.string.humidity))
        listInfoSecondView.add(ReInfo(weatherEntity.daily[1].humidity.toString(),R.string.humidity))
        listIncons.add(R.drawable.humidity)

        listInfo.add(ReInfo(weatherEntity.current.pressure.toString(),R.string.pressure))
        listInfoSecondView.add(ReInfo(weatherEntity.daily[1].pressure.toString(),R.string.pressure))
        listIncons.add(R.drawable.pressure)

        listInfo.add(ReInfo(weatherEntity.current.wind_speed.toString(),R.string.wind))
        listInfoSecondView.add(ReInfo(weatherEntity.daily[1].wind_speed.toString() + "km/h",R.string.wind))
        listIncons.add(R.drawable.wind)

        listInfo.add(ReInfo(weatherEntity.current.sunrise.toString(),R.string.sunrise))
        listInfoSecondView.add(ReInfo(sunriseFormatSecond,R.string.sunrise))
        listIncons.add(R.drawable.sunrise)

        listInfo.add(ReInfo(weatherEntity.current.sunset.toString(),R.string.sunset))
        listInfoSecondView.add(ReInfo(sunsetFormatSecond,R.string.sunset))
        listIncons.add(R.drawable.sunset)



        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adaptador
        }
        recyclerViewSecondView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adapterSecondView
        }
    }

    private fun initRecyclerHours(hours: List<Current>, recyclerView: RecyclerView){
        val adaptador = HoursAdapter(this,hours)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adaptador
        }
    }
    private fun initRecyclerDays(days: List<Daily>, recyclerView: RecyclerView){
        val adapterView = DaysAdapter(this,days)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterView
        }
    }

    private fun formatResponseCity(cityEntity: List<City>){
        val cityName = cityEntity[0].name
        val country = cityEntity[0].country
        val address = "$cityName, $country"

        binding.apply {
            tvLocationMain.text = address
        }
    }

    private fun formatResponse(weatherEntity: OneCall){

        var unitSymbol = "°C"

        if(units){
            unitSymbol = "°F"
        }

        try {
            val temp = "${weatherEntity.current.temp.toInt()}"
            val cityName = ""//weatherEntity.name
            val country = ""//weatherEntity.sys.country
            val address = "$cityName, $country"
            val tempMin = ""//"Min: ${weatherEntity.main.temp_min.toInt()}°"
            val tempMax = ""//"Max: ${weatherEntity.main.temp_max.toInt()}°"
            var status = ""
            val weatherDescription = weatherEntity.current.weather[0].description
            if(weatherDescription.isNotEmpty()){
                status = (weatherDescription[0].uppercaseChar() + weatherDescription.substring(1))
            }
            val dt = weatherEntity.current.dt
            val updateAt = "Actualizado: ${
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(dt * 1000))
            }"
            val sunrise = weatherEntity.current.sunrise
            val sunriseFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            val sunset = weatherEntity.current.sunset
            val sunsetFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            val wind = "${weatherEntity.current.wind_speed} km/h"
            val pressure = "${weatherEntity.current.pressure} mb"
            val humidity = "${weatherEntity.current.humidity}%"
            val feelsLike = getString(R.string.textSensacion) + " ${weatherEntity.current.feels_like.toInt()}$unitSymbol"
            val icon = weatherEntity.current.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"

            val iconSecond = weatherEntity.daily[1].weather.first().icon
            val iconUrlSecond = "https://openweathermap.org/img/w/$iconSecond.png"
            val tempInDayTom = weatherEntity.daily[1].temp.day.toInt().toString()
            val tempInNightTom = "/" + weatherEntity.daily[1].temp.night.toInt().toString() + unitSymbol
            var statusTom = ""
            val forecastTom = weatherEntity.daily[1].weather.first().description
            if(forecastTom.isNotEmpty()){
                statusTom = (forecastTom[0].uppercaseChar() + forecastTom.substring(1))
            }

            //binding.addressTextView.text = address

            binding.apply {
                tvDateMain.text = updateAt
                tvTemperatureMain.text = temp
                tvSimbolo.text = unitSymbol
                tvWeatherMain.text = status
                containerPrimaryMain.isVisible = true
                constraineLayoutContainerWeather.isVisible = true
                ivCardView.load(iconUrl)
                initRecycler(recyclerDay, recyclerViewNextHours, weatherEntity)
                initRecyclerHours(weatherEntity.hourly,recyclerViewNextHours)
                initRecyclerDays(weatherEntity.daily,recyclerDay)
            }

            showIndicator(false)
        }catch (exception: Exception){
            showError("Ha ocurrido un error con los datos")
            Log.e("Error format", "Ha ocurrido un error")
            showIndicator(false)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showIndicator(visible: Boolean) {
        binding.pbMainLoading.isVisible = visible
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, // TODO: Tenia un out antes del String
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                //Si el flujo es interrumpido, la solicitud de permiso es cancelada y se reciben arrays vacios.
                grantResults.isEmpty() -> Log.i(TAG, "La interaccion del usuario fue cancelada")

                //Permiso Otorgado
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation(this::setupViewData)

                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings) {
                        //Construye el intent que muestra la ventaa de configuracion del app
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", "mx.kodemia.climadefabiruchis", null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
