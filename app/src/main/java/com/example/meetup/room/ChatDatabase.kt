package com.example.meetup.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.meetup.room.model.Interest
import com.example.meetup.room.model.Message
import com.example.meetup.room.model.User
import com.example.meetup.room.model.UserInterest

@Database(
    entities = [
        User::class,
        Message::class,
        Interest::class,
        UserInterest::class
    ],
    version = 5,
    exportSchema = true
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {

        private var instance: ChatDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): ChatDatabase {
            if (instance == null) {
            }
            instance = Room.databaseBuilder(
                ctx.applicationContext, ChatDatabase::class.java,
                "meetUp_database"
            )
                .fallbackToDestructiveMigration()
                .build()
            return instance!!
        }

    }

}