package com.example.meetup.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.meetup.room.model.Message
import com.example.meetup.ui.main.MainActivity
import com.example.meetup.ui.main.viewmodel.MainViewModel

class FCMessageReceiver(val mainActivity: MainActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        /*Log.d("LOGGING", intent?.getStringExtra("messageid")+
                intent?.getStringExtra("senderid")+
                intent?.getStringExtra("receiverid")+
                intent?.getStringExtra("message")+
                intent?.getStringExtra("sendtime"))*/
        if(intent?.action == "com.example.meetup.newMessage") {
            val viewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)
            val messageid = intent.getStringExtra("messageid")?.toInt() ?: -1
            val senderid = intent.getStringExtra("senderid")?.toInt() ?: -1
            val receiverid = intent.getStringExtra("receiverid")?.toInt() ?: -1
            val message = intent.getStringExtra("message") ?: ""
            val sendtime = intent.getStringExtra("sendtime") ?: ""
            val newMessage = Message(messageid, senderid, receiverid, message!!, 0, "", sendtime)
            viewModel.mainRepo.chats.add(newMessage)
            viewModel.mainRepo.newMessage.value = viewModel.mainRepo.newMessage.value != true
        }
    }

}