package com.example.meetup.ui.login.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.meetup.R
import com.example.meetup.ui.login.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel : LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate (R.layout.fragment_login, container, false)
        val layoutSignUp = view.findViewById<View>(R.id.layout_signup_fragment)
        val layoutLogin = view.findViewById<View>(R.id.layout_login_fragment)
        val changeToSignUp = view.findViewById<TextView>(R.id.login_to_signUp)
        val changeToLogin = view.findViewById<TextView>(R.id.signup_to_login)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        val signUpButton = view.findViewById<Button>(R.id.sign_button)
        val loginEmail = view.findViewById<EditText>(R.id.login_email)
        val loginPassword = view.findViewById<EditText>(R.id.login_password)
        val signUpName = view.findViewById<EditText>(R.id.sign_name)
        val signUpNumber = view.findViewById<EditText>(R.id.sign_number)
        val signUpEmail = view.findViewById<EditText>(R.id.sign_email)
        val signUpPassword = view.findViewById<EditText>(R.id.sign_password)

        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        changeToSignUp.setOnClickListener {
            it.hideKeyboard()
            layoutLogin.visibility = View.GONE
            layoutSignUp.visibility = View.VISIBLE
        }
        changeToLogin.setOnClickListener {
            it.hideKeyboard()
            layoutSignUp.visibility = View.GONE
            layoutLogin.visibility = View.VISIBLE
        }
        loginButton.setOnClickListener {
            it.hideKeyboard()
            val stringEmail = loginEmail.text.toString()
            val stringPassword = loginPassword.text.toString()
            if(stringEmail.isEmpty() || stringPassword.isEmpty()) {
                Toast.makeText(requireActivity(),"Please fill all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.loginUser(stringEmail, stringPassword)
        }
        signUpButton.setOnClickListener {
            it.hideKeyboard()
            val stringName = signUpName.text.toString()
            val stringNumber = signUpNumber.text.toString()
            val stringEmail = signUpEmail.text.toString()
            val stringPassword = signUpPassword.text.toString()
            if (stringName.isEmpty() || stringEmail.isEmpty() || stringPassword.isEmpty()) {
                Toast.makeText(requireActivity(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            viewModel.signUpUser(stringName, stringEmail, stringPassword)
        }

        return view
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}