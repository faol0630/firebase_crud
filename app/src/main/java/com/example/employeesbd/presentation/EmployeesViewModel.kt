package com.example.employeesbd.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.domain.EmployeesRepoInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeesViewModel(
    private val employeesRepoInt: EmployeesRepoInt
): ViewModel() {

    fun fetchAllEmployeesVM() = liveData(Dispatchers.IO) {

        emit(ResourceSealedClass.Loading())

        try {
            emit(employeesRepoInt.getAllEmployees())
        } catch (e: Exception) {
            emit(ResourceSealedClass.Failure(e))
        }

    }

    fun getSingleEmployee(employeeId: Int) = liveData(Dispatchers.IO) {

        emit(ResourceSealedClass.Loading())

        try {
            emit(employeesRepoInt.getSingleEmployee(employeeId))
        }catch (e:Exception){
            emit(ResourceSealedClass.Failure(e))
        }

    }

    fun addNewEmployee(newEmployee: Employee, employeeName: String){
        viewModelScope.launch {
            employeesRepoInt.addNewEmployee(newEmployee, employeeName)
        }
    }


    fun deleteEmployee(employeeName: String){
        viewModelScope.launch {
            employeesRepoInt.deleteEmployee(employeeName)
        }
    }


}