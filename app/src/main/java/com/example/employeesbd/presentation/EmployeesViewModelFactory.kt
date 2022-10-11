package com.example.employeesbd.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.employeesbd.domain.EmployeesRepoInt

class EmployeesViewModelFactory(private val repo: EmployeesRepoInt): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(EmployeesRepoInt::class.java).newInstance(repo)
    }
}