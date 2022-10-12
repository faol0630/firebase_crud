package com.example.employeesbd.domain

import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.data.remote.RemoteDataSource

class EmployeesRepoImpl(
    private val dataSource: RemoteDataSource
) : EmployeesRepoInt {
    override suspend fun getAllEmployees(): ResourceSealedClass<List<Employee>> {
        return dataSource.getEmployeesFromFirebase()
    }

    override suspend fun getSingleEmployee(employeeId: Int): ResourceSealedClass<Employee?> {
        return dataSource.getSingleEmployee(employeeId)
    }

    override suspend fun addNewEmployee(newEmployee: Employee, employeeName: String) {
        dataSource.addNewEmployee(newEmployee, employeeName)
    }

    override suspend fun deleteEmployee(employeeName: String) {
        dataSource.deleteEmployee(employeeName)
    }
}