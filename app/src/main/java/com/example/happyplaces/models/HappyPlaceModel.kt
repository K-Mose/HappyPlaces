package com.example.happyplaces.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class HappyPlaceModel (
        val id: Int,
        val title: String?,
        val image: String?,
        val description: String?,
        val date: String?,
        val location: String?,
        val latitude: Double,
        val longitude: Double
):  Parcelable // Parcelable이 Serializable보다 빠르다 한다. // https://medium.com/android-news/parcelable-vs-serializable-6a2556d51538
{
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    // 주생성자와 순서 맞게 써야 함
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(this.id)
        dest!!.writeString(this.title)
        dest!!.writeString(this.image)
        dest!!.writeString(this.description)
        dest!!.writeString(this.date)
        dest!!.writeString(this.location)
        dest!!.writeDouble(this.latitude)
        dest!!.writeDouble(this.longitude)
    }

    override fun describeContents(): Int {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR
    }

    companion object CREATOR : Parcelable.Creator<HappyPlaceModel> {
        override fun createFromParcel(parcel: Parcel): HappyPlaceModel {
            return HappyPlaceModel(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlaceModel?> {
            return arrayOfNulls(size)
        }
    }
}
// Serializable // intent에 건네주는 것을 허용하는 형식으로 변경한다.