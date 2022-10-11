package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.core.hide
import com.example.employeesbd.core.show
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.data.remote.RemoteDataSource
import com.example.employeesbd.databinding.FragmentListBinding
import com.example.employeesbd.domain.EmployeesRepoImpl
import com.example.employeesbd.presentation.EmployeesViewModel
import com.example.employeesbd.presentation.EmployeesViewModelFactory


class ListFragment : Fragment(R.layout.fragment_list), EmployeesAdapter.OnEmployeeClickListener {

    private lateinit var binding : FragmentListBinding

    private val viewModel by viewModels<EmployeesViewModel>{
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListBinding.bind(view)

        viewModel.fetchAllEmployeesVM().observe(requireActivity()){
            when(it){
                is ResourceSealedClass.Loading -> {
                    binding.progressBar1.show()
                }
                is ResourceSealedClass.Success -> {
                    binding.progressBar1.hide()
                    if (it.data.isEmpty()){
                        binding.tvEmptyList.show()
                    }
                    binding.rvEmployeesList.adapter = EmployeesAdapter(it.data, this)
                }
                is ResourceSealedClass.Failure -> {
                    binding.progressBar1.hide()
                    Toast.makeText(requireContext(), "Error ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnAddNewEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addNewEmployeeFragment)
        }
    }

    override fun onEmployeeClick(employee: Employee, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("employee", employee)
        findNavController().navigate(R.id.action_listFragment_to_detailsFragment)

    }

}