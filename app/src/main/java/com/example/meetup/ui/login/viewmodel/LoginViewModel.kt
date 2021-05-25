package com.example.meetup.ui.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.meetup.repository.LoginRepository

class LoginViewModel(app: Application): AndroidViewModel(app) {

    val loginRepo = LoginRepository(app)

    init {
        loginRepo.chipSet.value = mutableSetOf()
    }
    fun signUpUser(stringName: String, stringEmail: String, stringPassword: String) {
        loginRepo.signUpUser(stringName, stringEmail, stringPassword)
    }
    fun loginUser(stringEmail: String, stringPassword: String) {
        loginRepo.loginUser(stringEmail, stringPassword)
    }

}