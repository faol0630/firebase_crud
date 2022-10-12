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

    private lateinit var binding : FragmentUpdateBinding

    private val viewModel by viewModels<EmployeesViewModel>{
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    private lateinit var employee: Employee

    //variables relacionadas con la foto:

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imageRef: StorageReference

    private lateinit var employeeName: String

    private lateinit var imageBitmap: Bitmap

    private lateinit var downloadUrl: String

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

        binding.btnAddUpdateEmployeee.setOnClickListener {
            viewModel.deleteEmployee(binding.tvUpdateEmployeeName.text.toString())

            employeeName = binding.tvUpdateEmployeeName.text.toString()

            sendPictureToDB(imageBitmap)

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

            val id = binding.etUpdateEmployeeId.text.toString()
            val occupation = binding.etOccupationUpdate.text.toString()
            val salary = binding.etSalaryUpdate.text.toString()
            val yearOfHire = binding.etYearOfHireUpdate.text.toString()
            val imageUrl = binding.tvUpdateEmployeeName.text.toString()
            val name = binding.tvUpdateEmployeeName.text.toString()

            val newEmployee = Employee(id.toInt(), name, occupation , salary.toInt(), yearOfHire.toInt(), imageUrl)

            val employeeName = binding.tvUpdateEmployeeName.text.toString()

            if (id.isNotEmpty() && name.isNotEmpty() && occupation.isNotEmpty() &&
                salary.isNotEmpty() && yearOfHire.isNotEmpty()
            ) {
                viewModel.addNewEmployee(newEmployee, employeeName)

                Toast.makeText(requireContext(), "Employee Updated", Toast.LENGTH_SHORT).show()

                sendPictureToDB(imageBitmap)
            }

        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                requireContext(),
                "There should be no empty boxes(IllegalArgumentException)",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "There should be no empty boxes(Exception)",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    //abrir la app que toma la foto:
    private fun dispatchTakePictureIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)//abre la app que controla la camara del cel
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)//take the picture
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "There isn't app to take the picture", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)  //data es la foto
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivItemUpdateEmployee.setImageBitmap(imageBitmap)//se asigna la foto en forma de bitmap al imageView

            uploadPicture()

        }
    }

    //se debe enviar la foto a storage para que de ahi coja la direccion http.
    //esa direccion es la que se pone en la base de datos firebase.

    //para subir la foto a firebase storage:
    private fun uploadPicture() {
        //instancia de storage para guardar la foto:
        val storageRef = FirebaseStorage.getInstance().reference
        //imagen dentro de una carpeta(imagenes) en storage:
        imageRef =
            storageRef.child("imagenes/${UUID.randomUUID()}.jpg")//para que se salve con nombres diferentes y no borre la anterior/imagenes crea una carpeta.

    }

    private fun sendPictureToDB(bitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        //comprimir la imagen:(calidad 100 es maxima calidad)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //convertir imagen en byteArray:
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)//promesa(parecido a sealed class)

        uploadTask.continueWithTask {
            //si no es exitoso:
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            //si es exitoso:
            imageRef.downloadUrl

        }.addOnCompleteListener {
            if (it.isSuccessful) {
                downloadUrl = it.result.toString()
                //para poner la foto dentro de firestore firebase como URL(update para no borrar la info previa):

                FirebaseFirestore.getInstance().collection("employees").document(employeeName)
                    .update(mapOf("imageUrl" to downloadUrl))

            }
        }


    }


}