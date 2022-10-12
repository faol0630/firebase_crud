package com.example.employeesbd.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.employeesbd.data.model.Employee
import com.example.employeesbd.databinding.ItemListBinding

class EmployeesAdapter(
    private val employeesList: List<Employee>,
    private val itemClickListener: OnEmployeeClickListener
): RecyclerView.Adapter<EmployeesAdapter.EmployeesViewHolder>() {

    interface OnEmployeeClickListener{
        fun onEmployeeClick(employee: Employee, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeesViewHolder {
        val itemBinding = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        val holder = EmployeesViewHolder(itemBinding, parent.context)
        itemBinding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf {
                it != RecyclerView.NO_POSITION
            } ?: return@setOnClickListener
            itemClickListener.onEmployeeClick(employeesList[position], position)
        }
        return holder
    }

    override fun onBindViewHolder(holder: EmployeesViewHolder, position: Int) {
        holder.bind1(employeesList[position])
    }

    override fun getItemCount(): Int {
       return employeesList.size
    }

    inner class EmployeesViewHolder(
        private val binding: ItemListBinding,
        val context: Context
    ):RecyclerView.ViewHolder(binding.root) {
        fun bind1(item: Employee) {
            Glide.with(context)
                .load(item.employeePicture)
                .centerCrop()
                .into(binding.employeeImageView)
            binding.tvEmployeeName.text = item.name
        }
    }

}