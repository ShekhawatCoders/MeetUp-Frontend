package com.example.meetup.ui.login.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.meetup.R
import com.example.meetup.ui.login.viewmodel.LoginViewModel


class SplashFragment : Fragment() {

    private lateinit var viewModel : LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        // check login
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        viewModel.loginRepo.isLogin()

        return view

    }

}