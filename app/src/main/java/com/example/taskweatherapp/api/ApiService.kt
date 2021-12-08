package com.example.taskweatherapp.api

import com.example.taskweatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather?&units=metric&APPID=4d3f79db8a9efaf46e52c66b9a9da301")
     fun getData(
        @Query("q") cityName:String ):Single<WeatherModel>

     @GET("data/2.5/weather?&units=metric&APPID=4d3f79db8a9efaf46e52c66b9a9da301")
     fun getDataByCoord(
         @Query("lat") latitude:String,
         @Query("lon") longitude:String
     ):Single<WeatherModel>

}