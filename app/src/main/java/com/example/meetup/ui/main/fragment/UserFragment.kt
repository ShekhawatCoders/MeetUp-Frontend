package com.example.meetup.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.adapter.FriendRecyclerAdapter
import com.example.meetup.room.model.User
import com.example.meetup.ui.chat.ChatActivity
import com.example.meetup.ui.main.viewmodel.MainViewModel

class UserFragment : Fragment(), FriendRecyclerAdapter.OnItemClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerAdapter : FriendRecyclerAdapter
    private lateinit var viewModel : MainViewModel

    companion object {
        private var mInstance = UserFragment()
        fun getInstance(): UserFragment {
            return mInstance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recyclerView = view.findViewById(R.id.user_recycler_view)


        if(!viewModel.mainRepo.friends.value.isNullOrEmpty()) {
            recyclerAdapter = FriendRecyclerAdapter(requireActivity(),
                viewModel.mainRepo.friends.value!!,this)
            recyclerView.adapter = recyclerAdapter
            recyclerAdapter.notifyDataSetChanged()
        }

        viewModel.mainRepo.friends.observe(requireActivity()) {
            recyclerAdapter = FriendRecyclerAdapter(requireActivity(),it,this)
            recyclerView.adapter = recyclerAdapter
            recyclerAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onItemClicked(user: User) {
        // go to chat fragment
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("userid",user.id)
        intent.putExtra("username",user.name)
        startActivity(intent)
    }

}