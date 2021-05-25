package com.example.meetup.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.meetup.NETWORK_REQ_CODE
import com.example.meetup.R
import com.example.meetup.ui.chat.ChatActivity
import com.example.meetup.ui.main.MainActivity
import com.example.meetup.ui.login.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel  = ViewModelProvider(this).get(LoginViewModel::class.java)

        if(!checkPermission())
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_NETWORK_STATE), NETWORK_REQ_CODE)

        val nm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        showNetworkState(nm)

        viewModel.loginRepo.loginStat.observe(this) {
            if(it) viewModel.loginRepo.userInterests()
            else {
                Navigation.findNavController(this, R.id.nav_host_login)
                        .navigate(R.id.splash_to_loginFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.splashFragment,true)
                                        .build())
            }
        }

        viewModel.loginRepo.userInterests.observe(this) { it ->
            if(it.isEmpty()) {
                if(viewModel.loginRepo.firstTime) {
                    Navigation.findNavController(this, R.id.nav_host_login)
                            .navigate(R.id.login_to_addInterestFragment,
                                    null,
                                    NavOptions.Builder().setPopUpTo(R.id.splashFragment, true)
                                            .build())
                } else {
                    Navigation.findNavController(this, R.id.nav_host_login)
                            .navigate(R.id.splash_to_addInterestFragment,
                                    null,
                                    NavOptions.Builder().setPopUpTo(R.id.splashFragment, true)
                                            .build())
                }
            } else {
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }, 2000)

                viewModel.loginRepo.networkConnection.observe(this) {
                   if(it) {
                        // if you want your app to start only on
                       // network available
                   }
                }

            }
        }
    }

    private fun showNetworkState(nm: ConnectivityManager) {
        nm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                //Network Connected
                viewModel.loginRepo.networkConnection.postValue(true)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                // NetWork Disconnected
                viewModel.loginRepo.networkConnection.postValue(false)
            }
        })
    }

    private fun checkPermission() : Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }
}