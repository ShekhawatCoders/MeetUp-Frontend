package com.example.meetup.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.meetup.LOG_TAG
import com.example.meetup.MainApplication
import com.example.meetup.NETWORK_REQ_CODE
import com.example.meetup.R
import com.example.meetup.adapter.SectionsPagerAdapter
import com.example.meetup.receiver.FCMessageReceiver
import com.example.meetup.ui.main.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var fcMessageReceiver: BroadcastReceiver
    private lateinit var filter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // fcMessageReceiver = FCMessageReceiver(this)
        // filter = IntentFilter("com.example.meetup.newMessage")

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if(!checkPermission())
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_NETWORK_STATE), NETWORK_REQ_CODE)

        val nm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        showNetworkState(nm)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        getToken()

        viewModel.mainRepo.callInitialMethod()

    }


    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val sharedPref = getSharedPreferences(resources.getString(R.string.SHARED_PREF), Context.MODE_PRIVATE)
                val id = sharedPref.getInt("id", -1)
                CoroutineScope(Dispatchers.IO).launch {
                    if (id != -1)
                        viewModel.mainRepo.updateToken(id, task.result.toString())
                }
            }
        }
    }

    private fun checkPermission() : Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun showNetworkState(nm: ConnectivityManager) {
        nm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                //Network Connected
                viewModel.mainRepo.networkConnection.postValue(true)
                viewModel.mainRepo.callInitialMethod()
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                // NetWork Disconnected
                viewModel.mainRepo.networkConnection.postValue(false)
            }
        })
    }

}