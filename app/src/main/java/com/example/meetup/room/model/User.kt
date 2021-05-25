package com.example.meetup.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class User (
        @PrimaryKey
        val id : Int,
        val name : String,
        val email : String,
        val password : String,
        val status : String?,
        val lastseen : String?,
        val socketid : String?,
        val token : String?
)