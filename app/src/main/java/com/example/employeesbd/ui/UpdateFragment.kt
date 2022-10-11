package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.databinding.FragmentUpdateBinding


class UpdateFragment : Fragment(R.layout.fragment_update) {

    private lateinit var binding : FragmentUpdateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUpdateBinding.bind(view)

        binding.btnAddUpdateEmployeee.setOnClickListener {

        }

        binding.btnGoingToListFragment.setOnClickListener {
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }
}