package com.example.meetup.ui.chat.fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import co.intentservice.chatui.ChatView
import co.intentservice.chatui.ChatView.TypingListener
import co.intentservice.chatui.models.ChatMessage
import com.example.meetup.*
import com.example.meetup.room.model.Message
import com.example.meetup.ui.chat.ChatActivity
import com.example.meetup.ui.chat.viewmodel.ChatViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var chatView : ChatView
    private lateinit var viewModel : ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val manager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = createNotificationChannel()
            manager.createNotificationChannel(channel)
        }

        viewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        chatView = view.findViewById(R.id.chat_view)
        toolbar = view.findViewById<MaterialToolbar>(R.id.chat_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = viewModel.chatRepo.friendName

        toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        addMessages(viewModel.chatRepo.chats)

        viewModel.chatRepo.newMessage.observe(requireActivity()) {
            addMessages(viewModel.chatRepo.chats)
        }

        chatView.setOnSentMessageListener { // perform actual message sending
            // viewModel.chatRepo.addMessage(chatView.inputEditText.text.toString())
            val obj = JSONObject()
            obj.put("senderid", viewModel.chatRepo.userId)
            obj.put("receiverid", viewModel.chatRepo.friendId)
            obj.put("message", chatView.inputEditText.text.toString())
            if(viewModel.chatRepo.mSocket?.connected() == true)
                viewModel.chatRepo.mSocket?.emit("newMessage", obj)
            else
                viewModel.chatRepo.addMessage(chatView.inputEditText.text.toString())
            true
        }

        chatView.setTypingListener(object : TypingListener {
            override fun userStartedTyping() {
                // will be called when the user starts typing
                // toolbar.subtitle = "Friend is typing ..."
                val obj = JSONObject()
                obj.put("id", viewModel.chatRepo.userId)
                obj.put("value", true)
                viewModel.chatRepo.mSocket?.emit("typing", obj)
            }

            override fun userStoppedTyping() {
                // will be called when the user stops typing
                val obj = JSONObject()
                obj.put("id", viewModel.chatRepo.userId)
                obj.put("value", false)
                viewModel.chatRepo.mSocket?.emit("typing", obj)
            }
        })

        // to enable options menu
        setHasOptionsMenu(true)

        viewModel.chatRepo.networkConnection.observe(requireActivity()) {
            if(it == true) {
                addSocketListener()
            } else {
                removeSocketListener()
            }
        }

        return view
    }

    private fun addSocketListener() {

        if(viewModel.chatRepo.mSocket?.connected() == false) {
            viewModel.chatRepo.mSocket?.connect()
        }

        viewModel.chatRepo.mSocket?.off()
        viewModel.chatRepo.mSocket?.on("typing", onTyping)
        viewModel.chatRepo.mSocket?.on("newMessage", onNewMessage)

    }

    private fun removeSocketListener() {
        viewModel.chatRepo.mSocket?.off()
        viewModel.chatRepo.mSocket?.disconnect()
    }

    private fun addMessages(messages: List<Message>) {
        chatView.clearMessages() // orientation change
        for(item in messages) {
            val type = if(item.senderid == viewModel.chatRepo.userId) ChatMessage.Type.SENT else ChatMessage.Type.RECEIVED
            val date = item.sendtime
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val parsedDate: Long = inputFormat.parse(date).time
            addMessage(item.message, parsedDate, type)
        }
    }

    private fun addMessage(message: String, parsedDate: Long ,type: ChatMessage.Type) {
        val msg = ChatMessage(message, parsedDate, type)
        chatView.addMessage(msg)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.clearChat -> {
                chatView.clearMessages()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
    }

    private fun createNotification(context: Context, title: String?, desc: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setSubText("Message")
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
    }

    private var onTyping = Emitter.Listener {
        val id = JSONObject(it[0].toString())["id"] as Int
        val value = JSONObject(it[0].toString())["value"] as Boolean
        CoroutineScope(Dispatchers.Main).launch {
            if (viewModel.chatRepo.friendId == id) {
                if (value)
                    toolbar.subtitle = viewModel.chatRepo.friendName + " is typing ..."
                else
                    toolbar.subtitle = null
            }
        }
    }

    private var onNewMessage = Emitter.Listener {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Message> = moshi.adapter(Message::class.java)
        val msg = adapter.fromJson(it[0].toString())

        val senderid = JSONObject(it[0].toString())["senderid"] as Int
        val receiverid = JSONObject(it[0].toString())["receiverid"] as Int


        if((senderid == viewModel.chatRepo.friendId &&
                receiverid == viewModel.chatRepo.userId) ||
                (senderid == viewModel.chatRepo.userId &&
                receiverid == viewModel.chatRepo.friendId)) {
            CoroutineScope(Dispatchers.Main).launch {
                if (msg != null) {
                    Log.d(LOG_TAG, "On New Message Listener:- $msg")
                    viewModel.chatRepo.addToLocalDatabase(msg)
                    viewModel.chatRepo.chats.add(msg)
                    viewModel.chatRepo.newMessage.postValue(
                            viewModel.chatRepo.newMessage.value != true)
                }
            }
        }
        else {
            /*if(receiverid == viewModel.chatRepo.userId) {
                val notification: Notification = createNotification(requireActivity(),
                        "New Message", "$message")
                manager.cancelAll()
                manager.notify(NOTIFICATION_ID, notification)
            }*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeSocketListener()
    }

    override fun onStart() {
        super.onStart()
        addSocketListener()
    }



}

