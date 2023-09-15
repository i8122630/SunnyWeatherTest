package com.example.nofifteenproject.logic

import androidx.lifecycle.liveData
import com.example.nofifteenproject.logic.model.Place
import com.example.nofifteenproject.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers


object Repository {

    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))

            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }

}