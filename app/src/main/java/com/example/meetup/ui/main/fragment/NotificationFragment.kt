package com.example.meetup.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.ui.main.viewmodel.MainViewModel

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recyclerView = view.findViewById(R.id.notification_recycler_view)


        return view
    }

}