package com.talatkuyuk.myexpenses.screens.main

import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("convert/?compact=ultra&apiKey=50fc9de4a729557262c5")
    suspend fun getExchangeRate(@Query("q") exchange: String) : Call<String>

    @GET("countries?apiKey=50fc9de4a729557262c5")
    suspend fun getCountries()

    @GET("USD/")
    suspend fun getCurrency() : Call<String>

    companion object {

        //var BASE_URL = "https://free.currconv.com/api/v7/"
        var BASE_URL = "http://10.0.2.2:3000/"


        fun create() : ApiInterface {

            val gson = GsonBuilder()
                .create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiInterface::class.java)

        }
    }
}

