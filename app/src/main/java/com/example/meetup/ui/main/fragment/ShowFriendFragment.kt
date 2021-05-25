package com.example.meetup.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.adapter.FriendRecyclerAdapter
import com.example.meetup.adapter.RequestRecyclerAdapter
import com.example.meetup.adapter.SuggestionRecyclerAdapter
import com.example.meetup.room.model.User
import com.example.meetup.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowFriendFragment : Fragment(),RequestRecyclerAdapter.OnItemClickListener,
SuggestionRecyclerAdapter.OnItemClickListener{

    private lateinit var requestRecyclerView : RecyclerView
    private lateinit var suggestionRecyclerView : RecyclerView
    private lateinit var requestRecyclerAdapter : RequestRecyclerAdapter
    private lateinit var suggestionRecyclerAdapter : SuggestionRecyclerAdapter
    private lateinit var viewModel : MainViewModel


    companion object {
        private var mInstance = ShowFriendFragment()
        fun getInstance(): ShowFriendFragment {
            return mInstance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_friend, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        requestRecyclerView = view.findViewById(R.id.request_recycler_view)
        suggestionRecyclerView = view.findViewById(R.id.suggestion_recycler_view)

        if(!viewModel.mainRepo.requests.value.isNullOrEmpty()) {
            requestRecyclerAdapter = RequestRecyclerAdapter(requireActivity(),
                viewModel.mainRepo.requests.value!!,this)
            requestRecyclerView.adapter = requestRecyclerAdapter
            requestRecyclerAdapter.notifyDataSetChanged()
        }

        if(!viewModel.mainRepo.suggestions.value.isNullOrEmpty()) {
            suggestionRecyclerAdapter = SuggestionRecyclerAdapter(requireActivity(),
                viewModel.mainRepo.suggestions.value!!,this)
            suggestionRecyclerView.adapter = suggestionRecyclerAdapter
            suggestionRecyclerAdapter.notifyDataSetChanged()
        }

        viewModel.mainRepo.suggestions.observe(requireActivity()) {
            suggestionRecyclerAdapter = SuggestionRecyclerAdapter(requireActivity(), it,this)
            suggestionRecyclerView.adapter = suggestionRecyclerAdapter
            suggestionRecyclerAdapter.notifyDataSetChanged()
        }

        viewModel.mainRepo.requests.observe(requireActivity()) {
            requestRecyclerAdapter = RequestRecyclerAdapter(requireActivity(), it,this)
            requestRecyclerView.adapter = requestRecyclerAdapter
            requestRecyclerAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onItemClickedRequest(user: User, flag: Boolean) {
        // accept request
        if(flag) {
            // user accepted request
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.mainRepo.makeFriends(user.id)
            }
        } else {
            // user declines request
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.mainRepo.removeFriendRequests(user.id)

            }
        }
    }

    override fun onItemClickedSuggestion(user: User, flag: Boolean) {
        // suggestion
        if(flag) {
            // user sent hello request
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.mainRepo.addFriendRequests(user.id)
            }
        } else {
            // remove from suggestion
            // store it locally
            CoroutineScope(Dispatchers.IO).launch {
                // TODO
            }
        }
    }

}