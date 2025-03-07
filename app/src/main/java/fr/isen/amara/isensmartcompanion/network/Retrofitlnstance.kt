package fr.isen.amara.isensmartcompanion.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: EventApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://isen-smart-companion-default-rtdb.europe-west1.firebasedatabase.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApiService::class.java)
    }
}
