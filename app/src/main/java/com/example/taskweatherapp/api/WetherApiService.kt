package com.example.taskweatherapp.api

import com.example.taskweatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WetherApiService {

    private val BASE_URL = "https://api.openweathermap.org/"
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

     fun getDataServiceByName(city:String): Single<WeatherModel>{
        return api.getData(city)
    }
    fun getDataServiceByCoord(longitude:String,latitude:String):Single<WeatherModel>{
        return api.getDataByCoord(longitude,latitude)
    }
}