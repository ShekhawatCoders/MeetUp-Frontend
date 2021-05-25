package com.example.meetup.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.meetup.BASE_URL
import com.example.meetup.LOG_TAG
import com.example.meetup.R
import com.example.meetup.room.ChatDatabase
import com.example.meetup.room.model.Message
import com.example.meetup.room.model.User
import com.example.meetup.service.WebService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


class MainRepository(val app: Application) {

    val friends = MutableLiveData<List<User>>()
    val suggestions = MutableLiveData<List<User>>()
    val requests = MutableLiveData<List<User>>()
    val chats = arrayListOf<Message>()
    var userId = -1
    val newMessage = MutableLiveData<Boolean>()
    val networkConnection = MutableLiveData<Boolean>()

    private val retrofit =  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    private val service = retrofit.create(WebService::class.java)
    private val sharedPref = app.getSharedPreferences(app.getString(R.string.SHARED_PREF), Context.MODE_PRIVATE)
    private val database = ChatDatabase.getInstance(app)

    //3 the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        userId = getId()
        newMessage.value = false
    }

    private fun getId() = sharedPref.getInt("id", 0)

    @WorkerThread
    suspend fun updateToken(id: Int, token: String) {
        if(networkConnection.value == false) {
        } else {
            try {
                service.updateToken(id, token).body()
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message)
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message)
            }
        }
    }
    @WorkerThread
    suspend fun getFriends(id: Int) {
        var items = database.chatDao().getFriends()
        friends.postValue(items)
        try {
            items = service.getFriends(id).body()
                    ?: emptyList()
            withContext(Dispatchers.IO) {
                // upload to local database
                database.chatDao().deleteFriends()
                database.chatDao().insertFriends(items)
            }
            friends.postValue(items)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, e.message)
        } catch(e: Exception) {
            Log.d(LOG_TAG, e.message)
        }
    }

    @WorkerThread
    suspend fun getFriendRequests(userId: Int) {
        if(networkConnection.value == false) {
            // get from local
        } else {
            try {
                val serviceData = service.getFriendRequests(userId).body()
                    ?: emptyList()
                requests.postValue(serviceData)
                Log.d(LOG_TAG, "getFriendRequests :- $serviceData")
                newMessage.postValue(newMessage.value != true)
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message)
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message)
            }
        }
    }
    @WorkerThread
    suspend fun getInterestedUsers(map : HashMap<String,String>) {
        if(networkConnection.value == false) {
        } else {
            try {
                val serviceData = service.allInterestedUsers(map).body()
                        ?: emptyList()
                updateSuggestions(serviceData)
                Log.d(LOG_TAG, "getInterestedUser :- $serviceData")
                newMessage.postValue(newMessage.value != true)
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message)
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message)
            }
        }
    }

    private fun updateSuggestions(serviceData: List<User>) {
        val items = arrayListOf<User>()
        for(user in serviceData) {
            if(friends.value?.contains(user) == true ||
                    user.id == userId)
                continue
            items.add(user)
        }
        suggestions.postValue(items)
    }

    @WorkerThread
    suspend fun getUserInterests(id: Int) {
        var items = database.chatDao().getUserInterests()
        if(items.isNullOrEmpty()) {
            try {
                items = service.userIntrests(id).body() ?: emptyList()
                database.chatDao().insertUserInterests(items)
                // delete prev interest and now add new interest
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message)
            } catch (e: Exception) {
                Log.d(LOG_TAG, e.message)
            }
        }
        withContext(Dispatchers.IO) {
            val map = hashMapOf<String, String>()
            for (item in items) {
                map[item.id.toString()] = item.name
            }
            getInterestedUsers(map)
        }
    }
    @WorkerThread
    suspend fun makeFriends(friendId: Int) {
        if(networkConnection.value == false) {
        } else {
            val serviceData = service.makeFriends(userId, friendId).body() ?: false
            withContext(Dispatchers.Main) {
                Toast.makeText(app, serviceData.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    @WorkerThread
    suspend fun addFriendRequests(friendId: Int) {
        if(networkConnection.value == false) {
        } else {
            val serviceData = service.addFriendRequests(userId, friendId).body() ?: false
            withContext(Dispatchers.Main) {
                Toast.makeText(app, serviceData.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    @WorkerThread
    suspend fun removeFriendRequests(friendId: Int) {
        if(networkConnection.value == false) {
        } else {
            val serviceData = service.removeFriendRequests(userId, friendId).body() ?: false
            withContext(Dispatchers.Main) {
                Toast.makeText(app, serviceData.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun callInitialMethod() {
        coroutineScope.launch {
            getFriends(userId)
            getUserInterests(userId)
            getFriendRequests(userId)
        }
    }

    private fun provideOkHttpClient(): OkHttpClient? {
        val okhttpClientBuilder = OkHttpClient.Builder()
        okhttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        okhttpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
        okhttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS)
        return okhttpClientBuilder.build()
    }
}