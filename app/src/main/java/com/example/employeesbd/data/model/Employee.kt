package com.example.employeesbd.data.model

import android.os.Parcel
import android.os.Parcelable

data class Employee(
    val id : Int = 0,
    val name : String = "",
    val occupation : String = "",
    val salary : Int = 0,
    val yearOfHire : Int = 0,
    val employeePicture: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(occupation)
        parcel.writeInt(salary)
        parcel.writeInt(yearOfHire)
        parcel.writeString(employeePicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Employee> {
        override fun createFromParcel(parcel: Parcel): Employee {
            return Employee(parcel)
        }

        override fun newArray(size: Int): Array<Employee?> {
            return arrayOfNulls(size)
        }
    }
}
