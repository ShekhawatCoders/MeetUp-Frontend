package com.example.meetup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.room.model.User
import com.google.android.material.button.MaterialButton

class RequestRecyclerAdapter(
        val context: Context,
        private val users: List<User>,
        private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<RequestRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.card_request_user_name)
        val acceptRequest: MaterialButton = itemView.findViewById(R.id.accept_request)
        val declineRequest: MaterialButton = itemView.findViewById(R.id.decline_request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_accept_request,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        with(holder) {
            nameText.text = user.name
            acceptRequest.setOnClickListener {
                // replace chat fragment with this user id
                itemClickListener.onItemClickedRequest(user, true)
            }
            declineRequest.setOnClickListener {
                itemClickListener.onItemClickedRequest(user, false)
            }
        }
    }

    override fun getItemCount() = users.size

    interface OnItemClickListener {
        fun onItemClickedRequest(user: User, flag: Boolean)
    }

}