package com.example.taskweatherapp

import android.Manifest
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.example.taskweatherapp.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel

    private lateinit var GET: SharedPreferences
    private  lateinit var fusedLocationClient: FusedLocationProviderClient
    var cName : String = "kyiv"
    var longitude:String = ""
    var latitude:String = ""
    val dropDownList = mutableListOf("Kyiv","Tokio","Paris","Istanbul","London")
    private lateinit var SET: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationContext.deleteDatabase("WEATHERDB")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()
        getLastKnownLocation()
        val adpater = ArrayAdapter(this,android.R.layout.simple_spinner_item,dropDownList)
        adpater.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_city.adapter = adpater
        spinner_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cName = parent?.getItemAtPosition(position).toString()
                if(parent?.getItemAtPosition(position).toString().equals("CurrentLocation")){

                    viewmodel.refreshDataByCoord(latitude,longitude)
                    }
                else {

                    viewmodel.refreshData(cName)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                cName = "kyiv"
            }

        }
        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewmodel.refreshData(cName!!)

        getLiveData()

        swipe_refresh_layout.setOnRefreshListener {
            ll_data.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            var cityName = cName
            viewmodel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }



    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
        else{
            dropDownList.add("CurrentLocation")
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if(it != null){
                latitude = it.latitude.toInt().toString()
                longitude= it.longitude.toInt().toString()
//                Toast.makeText(this, "$latitude $longitude", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLiveData() {

        viewmodel.weather_data.observe(this, Observer { data ->
            data?.let {
                ll_data.visibility = View.VISIBLE

                tv_city_code.text = data.sys.country.toString()
                tv_city_name.text = data.name.toString()

                tv_degree.text = data.main.temp.toString() + "°C"

                tv_humidity.text = data.main.humidity.toString() + "%"
                tv_wind_speed.text = data.wind.speed.toString()
                tv_lat.text = data.coord.lat.toString()
                tv_lon.text = data.coord.lon.toString()


                val helper = DBHelper(applicationContext)
                val db = helper.readableDatabase
                    val cv = ContentValues()
                    cv.put("CITYNAME", data.name.toString())
                    cv.put("CITYTEMP", data.main.temp)
                    cv.put("COUNTRY", data.sys.country)
                    db.insert("WEATHER", null, cv)

            }
        })

        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    tv_error.visibility = View.VISIBLE
                    pb_loading.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    tv_error.visibility = View.GONE
                }
            }
        })

        viewmodel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    pb_loading.visibility = View.GONE
                }
            }
        })

    }
}