package fr.isen.amara.isensmartcompanion.screens

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import fr.isen.amara.isensmartcompanion.models.Event
import fr.isen.amara.isensmartcompanion.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsViewModel : ViewModel() {
    private val _events = mutableStateListOf<Event>()
    val events: List<Event> get() = _events

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _events.clear()
                        _events.addAll(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("EventsViewModel", "Erreur de récupération des événements", t)
            }
        })
    }
}
