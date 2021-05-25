package com.example.meetup

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.example.meetup.service.WebService
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URISyntaxException

class MainApplication : Application() {
    // Here make a socket instance
    private val retrofit =  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    private val service = retrofit.create(WebService::class.java)

    // so that keeps alive though app
    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences(getString(R.string.SHARED_PREF), Context.MODE_PRIVATE)
        val userId = getId(sharedPref)

    }

    private fun getId(sharedPref: SharedPreferences) = sharedPref.getInt("id", 0)



}