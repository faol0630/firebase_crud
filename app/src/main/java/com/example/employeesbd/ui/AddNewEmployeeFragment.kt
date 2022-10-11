package com.example.employeesbd.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val viewModel by viewModels<EmployeesViewModel>{
        EmployeesViewModelFactory(EmployeesRepoImpl(RemoteDataSource()))
    }

    //variables relacionadas con la foto:

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imageRef: StorageReference

    private lateinit var employeeName: String

    private lateinit var imageBitmap: Bitmap

    private lateinit var downloadUrl: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNewEmployeeBinding.bind(view)

        binding.btnTakePicture.setOnClickListener {
            dispatchTakePictureIntent()

        }

        binding.btnAddNewEmployee.setOnClickListener {

            employeeName = binding.etEmployeeName.text.toString()

            addingNewEmployee()

            sendPictureToDB(imageBitmap)

            Toast.makeText(requireContext(), "New Employee Added", Toast.LENGTH_SHORT).show()
        }

        binding.btnGoingToListFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addNewEmployeeFragment_to_listFragment)

        }
    }

    private fun addingNewEmployee() {
        val id = binding.etNewEmployeeId.text.toString()
        val occupation = binding.etOccupation.text.toString()
        val salary = binding.etSalary.text.toString()
        val yearOfHire = binding.etYearOfHire.text.toString()
        val name = binding.etEmployeeName.text.toString()
        val imageUrl = binding.ivEmployeePicture.setImageBitmap(imageBitmap)//no cumple ninguna funcion

        //----------------------------------------------------------
        val newEmployee =
            Employee(id.toInt(), name, occupation, salary.toInt(), yearOfHire.toInt(), imageUrl.toString()
            )

        viewModel.addNewEmployee(newEmployee, employeeName)
    }

    //metodos relacionados con tomar la foto:

    //abrir la app que toma la foto:
    private fun dispatchTakePictureIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)//abre la app que controla la camara del cel
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)//toma la foto
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No existe app para tomar la foto", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //despues de presionar el chulo cuando la foto esta tomada:
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)  //data es la foto
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivEmployeePicture.setImageBitmap(imageBitmap)//se asigna la foto en forma de bitmap al imageView

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

                FirebaseFirestore.getInstance().collection("ciudades").document(employeeName)
                    .update(mapOf("imageUrl" to downloadUrl))


            }
        }

    }

}