package com.example.meetup.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userinterests")
data class UserInterest(
    @PrimaryKey
    val id : Int,
    val name : String
)