package com.example.myweather

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//485f3f4ea7a671a6c44483c145ed9623


class MainActivity : AppCompatActivity() {
    // ye neeche k binding waale code se  hum saare views me data show kara sakte hai
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("shahdol")
        SearchCity()


    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null){
                    fetchWeatherData(query)

                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }

        })


    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(weatherApi::class.java)

        val response = retrofit.getWeatherData(cityName, "485f3f4ea7a671a6c44483c145ed9623", "metric")
        response.enqueue(object: Callback<weatherApp>{
            override fun onResponse(p0: Call<weatherApp>, p1: Response<weatherApp>) {
                val responseBody = p1.body()
                if(p1.isSuccessful && responseBody!= null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    binding.temperature.text = "$temperature °C"
                    binding.humidity.text = "$humidity %"
                    binding.weather.text = "$condition"
                    binding.minTemp.text = "Min :$mintemp °C"
                    binding.maxTemp.text = "Max :$maxtemp °C"
                    binding.sealevel.text = "$sealevel hpa"
                    binding.windspeed.text = "$windspeed"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.condition.text = "$condition"
                    binding.date.text= date()
                        binding.day.text = dayName(System.currentTimeMillis())
                        binding.cityName.text= "$cityName"
                    //Log.d("TAG", "onResponse: $temperature")

                    changingBackgroundAccToCondition(condition)
                }
            }

            override fun onFailure(p0: Call<weatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changingBackgroundAccToCondition(conditionS: String) {
       when(conditionS){
           "Clear Sky" , "Sunny", "Clear" -> {
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView2.setAnimation(R.raw.sun)

           }

           "Light Rain" , "Drizzle", "Moderate rain", "Showers", "Heavy Rain" -> {
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieAnimationView2.setAnimation(R.raw.rain)

           }

           "Partly Clouds" , "Clouds", "Mist", "Foggy", "Overcast", "Haze" -> {
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView2.setAnimation(R.raw.cloud)

           }

           "Light Snow" , "Blizzard", "Moderate snow", "Heavy Snow" -> {
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieAnimationView2.setAnimation(R.raw.snow)

           }
           else ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView2.setAnimation(R.raw.sun)
           }

       }


        binding.lottieAnimationView2.playAnimation()
    }

    private fun date():String {

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}

