package com.example.employeesbd.domain

import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.data.model.Employee

interface EmployeesRepoInt {

    suspend fun getAllEmployees(): ResourceSealedClass<List<Employee>>

    suspend fun getSingleEmployee(employeeId: Int): ResourceSealedClass<Employee?>

    suspend fun addNewEmployee(newEmployee: Employee, employeeName: String)

    suspend fun deleteEmployee(employeeName: String)

}