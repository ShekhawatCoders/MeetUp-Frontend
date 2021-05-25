package com.example.meetup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.room.model.User

class FriendRecyclerAdapter(
        val context: Context,
        private val users: List<User>,
        val itemClickListener: OnItemClickListener): RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.card_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_user,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        with(holder) {
            nameText.text = user.name
            itemView.setOnClickListener {
                // replace chat fragment with this user id
                itemClickListener.onItemClicked(user)
            }
        }
    }

    override fun getItemCount() = users.size

    interface OnItemClickListener {
        fun onItemClicked(user: User)
    }

}