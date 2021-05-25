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

class SuggestionRecyclerAdapter(
        val context: Context,
        private val users: List<User>,
        private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<SuggestionRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.card_suggestion_user_name)
        val sendRequest: MaterialButton = itemView.findViewById(R.id.send_hello_request)
        val removeSuggestion: MaterialButton = itemView.findViewById(R.id.remove_suggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_suggestion_friend,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        with(holder) {
            nameText.text = user.name
            sendRequest.setOnClickListener {
                // replace chat fragment with this user id
                itemClickListener.onItemClickedSuggestion(user, true)
            }
            removeSuggestion.setOnClickListener {
                itemClickListener.onItemClickedSuggestion(user, false)
            }
        }
    }

    override fun getItemCount() = users.size

    interface OnItemClickListener {
        fun onItemClickedSuggestion(user: User, flag: Boolean)
    }

}