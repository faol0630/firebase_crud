package com.example.employeesbd.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.employeesbd.R
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.data.remote.RemoteDataSource
import com.example.employeesbd.databinding.FragmentAddNewEmployeeBinding
import com.example.employeesbd.domain.EmployeesRepoImpl
import com.example.employeesbd.presentation.EmployeesViewModel
import com.example.employeesbd.presentation.EmployeesViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class AddNewEmployeeFragment : Fragment(R.layout.fragment_add_new_employee) {

    private lateinit var binding: FragmentAddNewEmployeeBinding

    private val viewModel by viewModels<EmployeesViewModel> {
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imageRef: StorageReference

    private lateinit var employeeName: String

    private lateinit var imageBitmap: Bitmap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNewEmployeeBinding.bind(view)

        binding.btnTakePicture.setOnClickListener {
            dispatchTakePictureIntent()

        }

        binding.btnAddNewEmployee.setOnClickListener {

            employeeName = binding.etEmployeeName.text.toString()

            addingNewEmployee()

        }

        binding.btnGoingToListFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addNewEmployeeFragment_to_listFragment)

        }
    }

    private fun addingNewEmployee() {

        try {
            val id = binding.etNewEmployeeId.text.toString()
            val occupation = binding.etOccupation.text.toString()
            val salary = binding.etSalary.text.toString()
            val yearOfHire = binding.etYearOfHire.text.toString()
            val name = binding.etEmployeeName.text.toString()
            val imageUrl = ""

            val newEmployee =
                Employee(
                    id.toInt(),
                    name,
                    occupation,
                    salary.toInt(),
                    yearOfHire.toInt(),
                    imageUrl
                )

            viewModel.addNewEmployee(newEmployee, employeeName)

            Toast.makeText(requireContext(), "New Employee Added", Toast.LENGTH_SHORT).show()

            sendPictureToDB(imageBitmap)


        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                requireContext(),
                "There should be no empty boxes(IllegalArgumentException, Add New)",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(
                requireContext(),
                "lateinit property imageBitmap has not been initialized(UninitializedPropertyAccessException, Add New)",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "There should be no empty boxes(Exception, Add New)",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Can't find an app to take the photo", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivEmployeePicture.setImageBitmap(imageBitmap)

            uploadPicture()

        }
    }

    private fun uploadPicture() {
        val storageRef = FirebaseStorage.getInstance().reference
        imageRef =
            storageRef.child("imagenes/${UUID.randomUUID()}.jpg")

    }

    private fun sendPictureToDB(bitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)

        uploadTask.continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl

        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val downloadUrl = it.result.toString()

                FirebaseFirestore.getInstance().collection("employees").document(employeeName)
                    .update(mapOf("imageUrl" to downloadUrl))


            }
        }

    }

}