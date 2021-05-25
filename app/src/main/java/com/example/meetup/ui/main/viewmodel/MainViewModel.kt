package com.example.meetup.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.meetup.repository.MainRepository

class MainViewModel(app : Application) : AndroidViewModel(app) {

    var mainRepo : MainRepository = MainRepository(app)

}