package com.theappland.weatherforecastapp.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.theappland.weatherforecastapp.databinding.ActivityMainBinding
import com.theappland.weatherforecastapp.utils.placeHolderProgressBar
import com.theappland.weatherforecastapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var currentLat : Double? = null
    private var currentLong : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            getCurrentUserLocation()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        getCurrentUserLocation()
    }

    private fun observeLiveData() {
        viewModel.weather.observe(this) { weather ->
            weather?.let {
                if (weather.weather != null) {
                    Glide.with(this).setDefaultRequestOptions(RequestOptions().placeholder(placeHolderProgressBar(applicationContext))
                        .fitCenter()).load("https://openweathermap.org/img/wn/${weather.weather!![0].icon}@2x.png").into(binding.iconImageView)
                    binding.situationTextView.text = weather.weather!![0].situation
                }
                if (weather.degree != null && weather.degree?.temp != null) {
                    val celsius = (weather.degree!!.temp!!).roundToInt()
                    binding.degreeTextView.text = "$celsius°C"
                }
                weather.location?.let { location ->
                    binding.locationTextView.text = location
                }

                binding.timeTextView.text = viewModel.date()
            }
        }

        viewModel.loading.observe(this) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.iconImageView.visibility = View.GONE
                binding.degreeTextView.visibility = View.GONE
                binding.situationTextView.visibility = View.GONE
                binding.locationTextView.visibility = View.GONE
                binding.timeTextView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.iconImageView.visibility = View.VISIBLE
                binding.degreeTextView.visibility = View.VISIBLE
                binding.situationTextView.visibility = View.VISIBLE
                binding.locationTextView.visibility = View.VISIBLE
                binding.timeTextView.visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != "") {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.iconImageView.visibility = View.GONE
                binding.degreeTextView.visibility = View.GONE
                binding.situationTextView.visibility = View.GONE
                binding.locationTextView.visibility = View.GONE
                binding.timeTextView.visibility = View.GONE
            }
        }
    }

    private fun getCurrentUserLocation() {
        if (checkLocationPermissions()) {
            if (locationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location : Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Can't Found Location", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Got Location Successfully", Toast.LENGTH_SHORT).show()
                        currentLat = location.latitude
                        currentLong = location.longitude

                        val endPoint = "data/2.5/weather?lat=${currentLat}&lon=${currentLong}&appid={YOUR_API_KEY}}&units=metric"
                        viewModel.getWeather(endPoint)
                        observeLiveData()
                    }
                }
            } else {
                Toast.makeText(this, "Please enable location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun checkLocationPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun locationEnabled(): Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        private const val PERMISSION_ACCESS_LOCATION_RC = 1
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ACCESS_LOCATION_RC)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ACCESS_LOCATION_RC) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                getCurrentUserLocation()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}