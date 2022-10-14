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
import com.bumptech.glide.Glide
import com.example.employeesbd.R
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.data.remote.RemoteDataSource
import com.example.employeesbd.databinding.FragmentUpdateBinding
import com.example.employeesbd.domain.EmployeesRepoImpl
import com.example.employeesbd.presentation.EmployeesViewModel
import com.example.employeesbd.presentation.EmployeesViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class UpdateFragment : Fragment(R.layout.fragment_update) {

    private lateinit var binding: FragmentUpdateBinding

    private val viewModel by viewModels<EmployeesViewModel> {
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    private lateinit var employee: Employee

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imageRef: StorageReference

    private lateinit var employeeName: String

    private lateinit var imageBitmap: Bitmap

    private lateinit var imageUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            employee = it.getParcelable("employee")!!
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUpdateBinding.bind(view)

        updatingView()

        binding.btnTakeANewPicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnUpdateEmployeee.setOnClickListener {

            employeeName = binding.tvUpdateEmployeeName.text.toString()

            updatingEmployee()

        }

        binding.btnGoingToListFragment.setOnClickListener {
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }

    private fun updatingView() {
        binding.etUpdateEmployeeId.setText(employee.id.toString())
        binding.tvUpdateEmployeeName.text = employee.name
        binding.etOccupationUpdate.setText(employee.occupation)
        binding.etSalaryUpdate.setText(employee.salary.toString())
        binding.etYearOfHireUpdate.setText(employee.yearOfHire.toString())
        Glide.with(requireContext())
            .load(employee.imageUrl)
            .centerCrop()
            .into(binding.ivItemUpdateEmployee)
    }

    private fun updatingEmployee() {

        try {

            viewModel.deleteEmployee(binding.tvUpdateEmployeeName.text.toString())

            val id = binding.etUpdateEmployeeId.text.toString()
            val occupation = binding.etOccupationUpdate.text.toString()
            val salary = binding.etSalaryUpdate.text.toString()
            val yearOfHire = binding.etYearOfHireUpdate.text.toString()
            val name = binding.tvUpdateEmployeeName.text.toString()

            //saving the old photo in case it's not updated:
             imageUrl = employee.imageUrl

            val newEmployee = Employee(
                id.toInt(),
                name,
                occupation,
                salary.toInt(),
                yearOfHire.toInt(),
                imageUrl
            )

            val employeeName = binding.tvUpdateEmployeeName.text.toString()

            viewModel.addNewEmployee(newEmployee, employeeName)

            Toast.makeText(requireContext(), "Employee Updated", Toast.LENGTH_SHORT).show()

            sendPictureToDB(imageBitmap)


        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                requireContext(),
                "There should be no empty boxes(IllegalArgumentException, Update)",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(
                requireContext(),
                "The picture has not been changed",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    //opening the camera:
    private fun dispatchTakePictureIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "There isn't app to take the picture",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)  //date is the picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivItemUpdateEmployee.setImageBitmap(imageBitmap)

            imageUrl = Glide.with(requireContext())
                .load(imageBitmap)
                .centerCrop()
                .into(binding.ivItemUpdateEmployee).toString()

            uploadPicture()

        }
    }

    //first the picture is sent to firebase storage.then it's sent to firebase DB
    //to upload the picture to firebase storage:
    private fun uploadPicture() {
        val storageRef = FirebaseStorage.getInstance().reference
        //Saving with different names and doesn't delete the previous one."imagenes", is a folder.
        imageRef =
            storageRef.child("imagenes/${UUID.randomUUID()}.jpg")

    }

    private fun sendPictureToDB(bitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        //compressing the image: (quality 100 is maximum quality)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //converting image to byteArray:
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)//promise (similar to sealed class)

        uploadTask.continueWithTask {
            //Failure:
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            //Success:
            imageRef.downloadUrl

        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val downloadUrl = it.result.toString()
                //adding the picture inside firestore firebase as URL("update" instead of "set" so as not to erase the previous info):

                FirebaseFirestore.getInstance().collection("employees").document(employeeName)
                    .update(mapOf("imageUrl" to downloadUrl))
            }
        }
    }
}