package com.example.meetup.ui.chat

import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.meetup.MainApplication
import com.example.meetup.R
import com.example.meetup.ui.chat.viewmodel.ChatViewModel
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import java.net.URISyntaxException

class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val nm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        viewModel.chatRepo.friendId = intent.getIntExtra("userid",-1)
        viewModel.chatRepo.friendName= intent.getStringExtra("username")

        showNetworkState(nm)

    }

    private fun showNetworkState(nm: ConnectivityManager) {
        nm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                //Network Connected
                viewModel.chatRepo.callInitialMethod()
                // for syncing chat
                viewModel.chatRepo.networkConnection.postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // NetWork Disconnected
                viewModel.chatRepo.networkConnection.postValue(false)
            }
        })
    }



    override fun onResume() {
        super.onResume()
        viewModel.chatRepo.callInitialMethod()
    }

}