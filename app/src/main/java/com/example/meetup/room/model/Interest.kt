package com.example.meetup.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interests")
data class Interest(
        @PrimaryKey
        val id : Int,
        val name : String
)
