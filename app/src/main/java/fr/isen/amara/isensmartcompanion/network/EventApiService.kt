package fr.isen.amara.isensmartcompanion.network

import fr.isen.amara.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface EventApiService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
