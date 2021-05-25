package com.example.meetup.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.example.meetup.CHANNEL_ID
import com.example.meetup.CHANNEL_NAME
import com.example.meetup.NOTIFICATION_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {

    private var token: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val messageid = remoteMessage.data["messageid"]
        val senderid = remoteMessage.data["senderid"]
        val receiverid = remoteMessage.data["receiverid"]
        val message = remoteMessage.data["message"]
        val sendtime = remoteMessage.data["sendtime"]

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        /*
        val intent = Intent()
        intent.action = "com.example.meetup.newMessage"
        intent.putExtra("messageid", messageid)
        intent.putExtra("senderid", senderid)
        intent.putExtra("receiverid", receiverid)
        intent.putExtra("message", message)
        intent.putExtra("sendtime", sendtime)
        sendBroadcast(intent)
         */


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = createNotificationChannel()
            manager.createNotificationChannel(channel)
        }
        val notification: Notification = createNotification(this, title, "$message")
        // manager.notify(NOTIFICATION_ID, notification)

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
    }

    private fun createNotification(context: Context, title: String?, desc: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(desc)
                .setSubText(desc)
                .setSmallIcon(R.drawable.sym_contact_card)
                .setPriority(PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
    }

    fun getToken(): String? {
        return token
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        token = s
        Log.d("DATA", s)
    }

}