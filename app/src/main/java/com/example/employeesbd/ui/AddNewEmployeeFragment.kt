package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.databinding.FragmentAddNewEmployeeBinding


class AddNewEmployeeFragment : Fragment(R.layout.fragment_add_new_employee) {

    private lateinit var binding: FragmentAddNewEmployeeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNewEmployeeBinding.bind(view)

        binding.btnAddNewEmployee.setOnClickListener {
        }
        binding.btnGoingToListFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addNewEmployeeFragment_to_listFragment)

        }
    }

}