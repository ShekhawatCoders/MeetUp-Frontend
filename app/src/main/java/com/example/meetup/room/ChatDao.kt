package com.example.meetup.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.meetup.room.model.Interest
import com.example.meetup.room.model.Message
import com.example.meetup.room.model.User
import com.example.meetup.room.model.UserInterest

@Dao
interface ChatDao {

    @Query("SELECT * FROM interests")
    fun getInterests(): List<Interest>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInterests(interests: List<Interest>)
    @Query("DELETE FROM interests")
    fun deleteInterests()

    @Query("SELECT * FROM userinterests")
    fun getUserInterests(): List<UserInterest>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInterests(interests: List<UserInterest>)
    @Query("DELETE FROM userinterests")
    fun deleteUserInterests()

    @Query("SELECT * FROM friends")
    fun getFriends(): List<User>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriends(users : List<User>)
    @Query("DELETE FROM friends")
    fun deleteFriends()

    @Query("SELECT * FROM chats WHERE " +
            "(senderid = :senderId AND receiverid = :receiverId) " +
            "OR (senderid = :receiverId AND receiverid = :senderId)")
    fun getChats(senderId: Int,receiverId: Int): List<Message>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(message: Message)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChats(message: List<Message>)
    @Query("DELETE FROM chats")
    fun deleteChats()

}