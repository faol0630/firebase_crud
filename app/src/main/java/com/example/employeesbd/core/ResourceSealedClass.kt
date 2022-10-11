package com.example.employeesbd.core

sealed class ResourceSealedClass<out T> {
    class Loading<out T>: ResourceSealedClass<T>()
    data class Success<out T>(val data: T): ResourceSealedClass<T>()
    data class Failure(val exception: Exception): ResourceSealedClass<Nothing>()
}
