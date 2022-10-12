package com.example.employeesbd.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.employeesbd.R
import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.core.hide
import com.example.employeesbd.core.show
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.data.remote.RemoteDataSource
import com.example.employeesbd.databinding.FragmentDetailsBinding
import com.example.employeesbd.domain.EmployeesRepoImpl
import com.example.employeesbd.presentation.EmployeesViewModel
import com.example.employeesbd.presentation.EmployeesViewModelFactory


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var binding : FragmentDetailsBinding

    private val viewModel by viewModels<EmployeesViewModel>{
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    private lateinit var employee: Employee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            employee = it.getParcelable("employee")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetailsBinding.bind(view)

        val employeeId = employee.id

        viewModel.getSingleEmployee(employeeId).observe(requireActivity()){
            when(it){
                is ResourceSealedClass.Loading -> {
                    binding.progressBar1.show()
                }
                is ResourceSealedClass.Success -> {
                    binding.progressBar1.hide()
                    updatingView()
                }
                is ResourceSealedClass.Failure -> {
                    binding.progressBar1.hide()
                    Toast.makeText(requireContext(), "Error ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnUpdateEmployee.setOnClickListener {

            val bundle = Bundle()
            bundle.putParcelable("employee", employee)
            findNavController().navigate(R.id.action_detailsFragment_to_updateFragment, bundle)

        }

        binding.btnDeleteEmployee.setOnClickListener {

            deleteEmployee()

            Toast.makeText(requireContext(), "Employee deleted", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_detailsFragment_to_listFragment)
        }
    }

    private fun deleteEmployee() {
        val name = binding.tvEmployeeNameDetails.text.toString()
        viewModel.deleteEmployee(name)
    }

    private fun updatingView() {
        binding.tvEmployeeIdDetails.text = employee.id.toString()
        binding.tvEmployeeNameDetails.text = employee.name
        binding.tvOccupationDetails.text = employee.occupation
        binding.tvSalaryDetails.text = employee.salary.toString()
        binding.tvYearOfHireDetails.text = employee.yearOfHire.toString()
        Glide.with(requireContext())
            .load(employee.employeePicture)
            .centerCrop()
            .into(binding.ivEmployeePictureDetails)
    }
}