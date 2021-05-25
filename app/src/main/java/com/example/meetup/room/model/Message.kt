package com.example.meetup.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "chats")
data class Message(
    @PrimaryKey
    val messageid : Int,
    val senderid : Int,
    val receiverid : Int,
    val message : String,
    val messagetype : Int,
    val filelink : String?,
    val sendtime : String
)
