package com.example.meetup.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.meetup.BASE_URL
import com.example.meetup.LOG_TAG
import com.example.meetup.R
import com.example.meetup.room.ChatDatabase
import com.example.meetup.room.model.Message
import com.example.meetup.service.WebService
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URISyntaxException

class ChatRepository(val app: Application) {

    var friendName = ""
    val chats = arrayListOf<Message>()
    var userId = -1
    var friendId = -1
    val newMessage = MutableLiveData<Boolean>()
    val networkConnection = MutableLiveData<Boolean>()
    var mSocket: Socket? = getSocket()

    private val retrofit =  Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    private val service = retrofit.create(WebService::class.java)
    private val sharedPref = app.getSharedPreferences(app.getString(R.string.SHARED_PREF), Context.MODE_PRIVATE)
    private val database = ChatDatabase.getInstance(app)

    init {
        userId = getId()
        newMessage.value = false
    }

    fun addMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            uploadMessage(userId,friendId,message)
        }
    }

    private fun getId() = sharedPref.getInt("id", 0)

    @WorkerThread
    suspend fun getOneToOneChats(receiverId: Int) {
        val serviceData = database.chatDao().getChats(userId, receiverId)
        chats.clear()
        chats.addAll(serviceData)
        newMessage.postValue(newMessage.value != true)
        if(networkConnection.value == true) {
            try {
                val serviceData = service.getChatsOneToOne(userId, receiverId).body() ?: emptyList()
                chats.clear()
                chats.addAll(serviceData)
                database.chatDao().deleteChats()
                database.chatDao().insertChats(serviceData)
                newMessage.postValue(newMessage.value != true)
                Log.d(LOG_TAG, serviceData.toString())
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message)
            } catch (e: Exception) {
                Log.d(LOG_TAG, e.message)
            }
        }
    }

    @WorkerThread
    suspend fun uploadMessage(userId: Int,friendId: Int,message: String) {
        if(networkConnection.value == false) {
        } else {
            try {
                val serviceData = service.addChatOneMesssage(userId, friendId, message).body()
                    ?: emptyList()
                // chats.clear()
                chats.add(serviceData[0])
                addToLocalDatabase(serviceData[0])
                newMessage.postValue(newMessage.value != true)
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message ?: "Error")
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message ?: "Error")
            }
        }
    }

    fun callInitialMethod() {
        CoroutineScope(Dispatchers.IO).launch {
            getOneToOneChats(friendId)
            // getUserInterests(userId)
            // getFriendRequests(userId)
            // getFriends(userId)
            // getAllChats(userId)
        }
    }

    private fun getSocket() : Socket? {

        var mSocket: Socket? = null

        val options = IO.Options()
        options.transports = arrayOf(WebSocket.NAME)
        options.reconnection = true //reconnection
        options.query = "userId=$userId"
        options.forceNew = true

        try {
            //creating socket instance
            mSocket = IO.socket("https://meetup-app7663.herokuapp.com/", options)
        } catch (e: URISyntaxException) {
            // Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            Log.d(LOG_TAG, e.message ?: "Error")
        }

        mSocket?.connect()

        return mSocket
    }


    @WorkerThread
    suspend fun updateLastSeen(userId: Int, online: Boolean) {
        if(online) {
            service.updateLastSeen(userId,"online")
        } else {
            val time = System.currentTimeMillis().toString()
            service.updateLastSeen(userId, time)
        }
    }

    fun addToLocalDatabase(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            database.chatDao().insertChat(message)
        }
    }

}