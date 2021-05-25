package com.example.meetup.service

import com.example.meetup.room.model.Interest
import com.example.meetup.room.model.Message
import com.example.meetup.room.model.User
import com.example.meetup.room.model.UserInterest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface WebService {

    @GET("api/v1/getFriends")
    suspend fun getFriends(
            @Query("id") id: Int
    ) : Response<List<User>>

    @GET("api/v1/getFriendRequests")
    suspend fun getFriendRequests(
            @Query("id") id: Int
    ) : Response<List<User>>

    @GET("api/v1/makeFriends")
    suspend fun makeFriends(
            @Query("firstid") firstId: Int,
            @Query("secondid") secondId: Int
    ) : Response<Boolean>

    @GET("api/v1/addFriendRequests")
    suspend fun addFriendRequests(
            @Query("firstid") firstId: Int,
            @Query("secondid") secondId: Int
    ) : Response<Boolean>

    @GET("api/v1/removeFriendRequests")
    suspend fun removeFriendRequests(
            @Query("firstid") firstId: Int,
            @Query("secondid") secondId: Int
    ) : Response<Boolean>

    @GET("api/v1/addChatOneMessage")
    suspend fun addChatOneMesssage(
            @Query("senderid") senderId: Int,
            @Query("receiverid") receiverId: Int,
            @Query("message") message: String
    ): Response<List<Message>>

    @GET("api/v1/userInterests")
    suspend fun userIntrests(
            @Query("userid") userId: Int
    ): Response<List<UserInterest>>

    @FormUrlEncoded
    @POST("api/v1/allInterestedUsers")
    suspend fun allInterestedUsers(
            @FieldMap(encoded = true) map: HashMap<String,String>
    ) : Response<List<User>>

    @GET("api/v1/allInterests")
    suspend fun getInterests() : Response<List<Interest>>

    @GET("api/v1/chatOne")
    suspend fun getChatsOneToOne(
        @Query("senderid") senderId: Int,
        @Query("receiverid") receiverId: Int
    ) : Response<List<Message>>

    @GET("api/v1/chatAll")
    suspend fun getChatsAll(
        @Query("id") id: Int,
    ) : Response<List<Message>>

    @GET("api/v1/groupChatAll")
    suspend fun getGroupChatsAll(
        @Query("id") id: Int,
    ) : Response<List<Message>>

    @GET("api/v1/login")
    suspend fun loginUser(
            @Query("email") email : String,
            @Query("password") password : String
    ) : Response<List<User>>

    @GET("api/v1/signUp")
    suspend fun signUpUser(
            @Query("name") name : String,
            @Query("email") email : String,
            @Query("password") password : String
    ) : Response<List<User>>

    @FormUrlEncoded
    @POST("api/v1/addInterests")
    suspend fun addInterest(
            @FieldMap(encoded = true) map: HashMap<String,String>
    ) : Response<Boolean>

    @GET("api/v1/updateToken")
    suspend fun updateToken(
            @Query("id") id : Int,
            @Query("token") token : String
    ) : Response<Boolean>

    @GET("api/v1/updateLastSeen")
    suspend fun updateLastSeen(
            @Query("id") id : Int,
            @Query("lastseen") lastSeen : String
    ) : Response<Boolean>

}
