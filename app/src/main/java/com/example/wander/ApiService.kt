package com.example.wander

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory


import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("api/directions/json")
    fun getDistance (@Query("key") key: String,
                          @Query("origin") origin: String,
                          @Query("destination") destination: String
    ) : Call<DirectionResponses>
}






// Configure retrofit to parse JSON and use coroutines

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


object MapApi {
    val retrofitService : ApiService by lazy { retrofit.create(ApiService::class.java) }
}

