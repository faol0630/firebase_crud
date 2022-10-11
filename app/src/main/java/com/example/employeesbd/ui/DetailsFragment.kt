package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var binding : FragmentDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetailsBinding.bind(view)

        binding.btnUpdateEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_updateFragment)
        }

        binding.btnDeleteEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_listFragment)
        }
    }
}