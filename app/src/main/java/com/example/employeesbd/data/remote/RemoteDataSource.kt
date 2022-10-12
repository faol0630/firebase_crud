package com.example.employeesbd.data.remote

import com.example.employeesbd.core.ResourceSealedClass
import com.example.employeesbd.data.model.Employee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RemoteDataSource {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getEmployeesFromFirebase(): ResourceSealedClass<List<Employee>> {
        val employeesList: MutableList<Employee> = mutableListOf()
        val querySnapshot = FirebaseFirestore
            .getInstance()
            .collection("employees")
            .get()
            .await()

        //querySnapshot is the entire list of firebase documents
        for (i in querySnapshot.documents) {
            i.toObject(Employee::class.java)?.let {
                employeesList.add(it)
            }
        }
        return ResourceSealedClass.Success(employeesList)
    }

    suspend fun getSingleEmployee(employeeId: Int): ResourceSealedClass<Employee?> {

        val employee1 = db.collection("employees")
            .document(employeeId.toString())
            .get()
            .await()
            .toObject(Employee::class.java)

        return ResourceSealedClass.Success(employee1)

    }

    suspend fun addNewEmployee(newEmployee: Employee, employeeName: String) {

        db.collection("employees")
            .document(employeeName)
            .set(newEmployee)
            .await()

    }

    suspend fun deleteEmployee(employeeName: String) {

        db.collection("employees")
            .document(employeeName)
            .delete()
            .await()

    }


}