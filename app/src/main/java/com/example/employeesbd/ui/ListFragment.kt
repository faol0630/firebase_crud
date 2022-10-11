package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.databinding.FragmentListBinding


class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding : FragmentListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListBinding.bind(view)

        binding.btnAddNewEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addNewEmployeeFragment)
        }
    }

}