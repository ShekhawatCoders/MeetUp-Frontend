package com.example.meetup.ui.login.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.meetup.R
import com.example.meetup.ui.login.viewmodel.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AddInterestFragment : Fragment() {

    private lateinit var viewModel : LoginViewModel
    private lateinit var listView : ListView
    private lateinit var interests : ArrayList<String>
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipCountText: TextView
    private lateinit var addInterestButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_add_interest, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        listView = view.findViewById(R.id.chip_list_view)
        chipGroup = view.findViewById(R.id.chip_list_group)
        chipCountText = view.findViewById(R.id.notify_chip_count)
        addInterestButton = view.findViewById(R.id.btn_add_interest)

        viewModel.loginRepo.interests()

        chipGroup.clearCheck()
        for(item in viewModel.loginRepo.chipSet.value ?: emptySet()) {
            addChipItem(item)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val name = interests[position]
            if(viewModel.loginRepo.chipSet.value?.contains(name) == true) {
                return@setOnItemClickListener
            }
            addChipItem(name)
        }

        addInterestButton.setOnClickListener {
            // create array list and send it
            viewModel.loginRepo.addInterests()

        }

        viewModel.loginRepo.interests.observe(requireActivity()) {
            interests = ArrayList()
            for(item in it) {
                interests.add(item.name)
            }
            adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, interests)
            listView.adapter = adapter
        }

        return view

    }

    private fun addChipItem(name: String) {
        val chip = Chip(requireActivity())
        chip.text = name
        chip.isCloseIconVisible = true
        chipGroup.addView(chip)
        viewModel.loginRepo.chipSet.value?.add(name)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
            viewModel.loginRepo.chipSet.value?.remove(name)
            checkChipCount()
        }
        checkChipCount()
    }

    private fun checkChipCountUtils(): Boolean {
        return viewModel.loginRepo.chipSet.value?.size == 0
    }

    private fun checkChipCount() {
        if(checkChipCountUtils()) {
            chipCountText.visibility = View.VISIBLE
        } else {
            chipCountText.visibility = View.GONE
        }
    }

}