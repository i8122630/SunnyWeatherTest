package com.example.nofifteenproject.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nofifteenproject.R
import com.example.nofifteenproject.logic.Repository.refreshWeather
import com.example.nofifteenproject.logic.model.Weather
import com.example.nofifteenproject.logic.model.getSky
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    private lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var drawerLayout:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        drawerLayout = findViewById(R.id.drawerLayout)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(R.color.purple_200)
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        refreshWeather()

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })


        findViewById<Button>(R.id.navBtn).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }


    private fun showWeatherInfo(weather: Weather) {
        findViewById<TextView>(R.id.placeName).text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        findViewById<TextView>(R.id.currentTemp).text = currentTempText
        findViewById<TextView>(R.id.currentSky).text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        findViewById<TextView>(R.id.currentAQI).text = currentPM25Text
        findViewById<RelativeLayout>(R.id.nowLayout).setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        findViewById<LinearLayout>(R.id.forecastLayout).removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                findViewById<LinearLayout>(R.id.forecastLayout), false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            findViewById<LinearLayout>(R.id.forecastLayout).addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        findViewById<TextView>(R.id.coldRiskText).text = lifeIndex.coldRisk[0].desc
        findViewById<TextView>(R.id.dressingText).text = lifeIndex.dressing[0].desc
        findViewById<TextView>(R.id.ultravioletText).text = lifeIndex.ultraviolet[0].desc
        findViewById<TextView>(R.id.carWashingText).text = lifeIndex.carWashing[0].desc
        findViewById<ScrollView>(R.id.weatherLayout).visibility = View.VISIBLE
    }


}