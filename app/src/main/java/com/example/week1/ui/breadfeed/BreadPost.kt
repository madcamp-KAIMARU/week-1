package com.example.week1.ui.breadfeed

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class BreadPost(
    val id: String = UUID.randomUUID().toString(),
    val imageUrl: String,
    val description: String,
    val date: String,
    var currentParticipants: Int,
    val maxParticipants: Int,
    val where2Meet: String,
    var hasJoined: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: UUID.randomUUID().toString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeInt(currentParticipants)
        parcel.writeInt(maxParticipants)
        parcel.writeString(where2Meet)
        parcel.writeByte(if (hasJoined) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BreadPost> {
        override fun createFromParcel(parcel: Parcel): BreadPost {
            return BreadPost(parcel)
        }

        override fun newArray(size: Int): Array<BreadPost?> {
            return arrayOfNulls(size)
        }
    }
}