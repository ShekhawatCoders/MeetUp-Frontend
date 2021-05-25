package com.example.meetup.repository

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.meetup.BASE_URL
import com.example.meetup.LOG_TAG
import com.example.meetup.R
import com.example.meetup.room.ChatDatabase
import com.example.meetup.room.model.Interest
import com.example.meetup.room.model.User
import com.example.meetup.room.model.UserInterest
import com.example.meetup.service.WebService
import com.example.meetup.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginRepository(val app : Application) {

    val networkConnection = MutableLiveData<Boolean>() // network connection
    var chipSet =  MutableLiveData<MutableSet<String>>() // add interest
    val loginStat = MutableLiveData<Boolean>() // login state
    var firstTime = false
    val interests = MutableLiveData<List<Interest>>() // all interests
    val userInterests = MutableLiveData<List<UserInterest>>() // user interests

    private val sharedPref = app.getSharedPreferences(app.getString(R.string.SHARED_PREF), Context.MODE_PRIVATE)
    private val retrofit =  Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    private val service: WebService = retrofit.create(WebService::class.java)
    private val database = ChatDatabase.getInstance(app) // local database

    @WorkerThread
    suspend fun getLoginUser(email : String,password : String) {
        if(networkConnection.value == false) {
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "No Network Connection", Toast.LENGTH_SHORT).show()
            }
        } else {
            val serviceData = service.loginUser(email, password).body() ?: emptyList()
            withContext(Dispatchers.Main) {
                if (serviceData.isEmpty()) {
                    Toast.makeText(app, "Login error : Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
            if (serviceData.isNotEmpty()) saveUser(serviceData[0])
        }
    }
    @WorkerThread
    suspend fun getSignUpUser(name: String,email: String,password: String) {
        if(networkConnection.value == false) {
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "No Network Connection", Toast.LENGTH_SHORT).show()
            }
        } else {
            val serviceData = service.signUpUser(name, email, password).body() ?: emptyList()
            withContext(Dispatchers.Main) {
                if (serviceData.isEmpty()) {
                    Toast.makeText(app, "SignUp error : " +
                            "If you have already have an account with this email " +
                            "then please try to login",
                            Toast.LENGTH_SHORT).show()
                }
            }
            if (serviceData.isNotEmpty()) saveUser(serviceData[0])
        }
    }
    @WorkerThread
    suspend fun getAddInterests(map : HashMap<String,String>) {
        if(networkConnection.value == false) {
        } else {
            val serviceData = service.addInterest(map).body() ?: false
            withContext(Dispatchers.Main) {
                if (!serviceData) {
                    Toast.makeText(app, "Error !!!", Toast.LENGTH_SHORT).show()
                } else {
                    // add to local database
                    val items = arrayListOf<UserInterest>()
                    for (item in map.entries) {
                        items.add(UserInterest(item.key.toInt(), item.value))
                    }
                    withContext(Dispatchers.IO) {
                        database.chatDao().insertUserInterests(items)
                    }
                    Toast.makeText(app, "Interests Added Successfully !!!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(app, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    app.startActivity(intent)
                }
            }
        }
    }
    @WorkerThread
    suspend fun getInterests() {
        if(networkConnection.value == false) {
        } else {
            try {
                val serviceData = service.getInterests().body() ?: emptyList()
                interests.postValue(serviceData)
                // delete prev interest and now add new interest
                withContext(Dispatchers.IO) {
                    database.chatDao().deleteInterests()
                    database.chatDao().insertInterests(serviceData)
                }
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message ?: "Error")
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message ?: "Error")
            }
        }
    }
    @WorkerThread
    suspend fun getUserInterests(id: Int) {
        if(networkConnection.value == false) {
        } else {
            try {
                val serviceData = service.userIntrests(id).body() ?: emptyList()
                // delete prev interest and now add new interest
                withContext(Dispatchers.IO) {
                    database.chatDao().deleteUserInterests()
                    database.chatDao().insertUserInterests(serviceData)
                    userInterests.postValue(serviceData)
                }
            } catch (e: Throwable) {
                Log.d(LOG_TAG, e.message ?: "Error")
            } catch(e: Exception) {
                Log.d(LOG_TAG, e.message ?: "Error")
            }
        }
    }

    private fun saveUser(user: User) {
        with(sharedPref.edit()) {
            putBoolean("login", true)
            putInt("id", user.id)
            putString("name", user.name)
            putString("email", user.email)
            putString("password", user.password)
            putString("token", user.token)
            putString("socketid", user.socketid)
            commit()
        }
        loginStat.postValue(true)
        firstTime = true
    }

    fun isLogin() : Boolean {
        return sharedPref.getBoolean("login",false).also { this.loginStat.value = it }
    }
    fun signUpUser(stringName: String, stringEmail: String, stringPassword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getSignUpUser(stringName, stringEmail, stringPassword)
        }
    }
    fun loginUser(stringEmail: String, stringPassword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getLoginUser(stringEmail, stringPassword)
        }
    }

    private fun getUserId() = sharedPref.getInt("id", 0)

    fun addInterests() {
        if(interests.value.isNullOrEmpty()) return
        val map = HashMap<String,String>()
        val id = getUserId().toString()
        for(item in interests.value!!)
            if(chipSet.value?.contains(item.name) == true)
                map[item.id.toString()] = id
        CoroutineScope(Dispatchers.IO).launch {
            getAddInterests(map)
        }
    }
    fun interests() {
        CoroutineScope(Dispatchers.IO).launch {
            val items = database.chatDao().getInterests()
            if (items.isEmpty()) {
                getInterests()
            } else {
                interests.postValue(items)
            }
        }
    }

    fun userInterests() {
        CoroutineScope(Dispatchers.IO).launch {
            val items = database.chatDao().getUserInterests()
            if (items.isEmpty()) {
                getUserInterests(getUserId())
            } else {
                userInterests.postValue(items)
            }
        }
    }
}