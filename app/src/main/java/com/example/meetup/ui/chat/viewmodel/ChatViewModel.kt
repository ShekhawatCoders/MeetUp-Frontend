package com.example.meetup.ui.chat.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.meetup.repository.ChatRepository

class ChatViewModel(app: Application): AndroidViewModel(app)  {

    val chatRepo = ChatRepository(app)

}