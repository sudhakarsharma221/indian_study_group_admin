package com.indiastudygroupadminapp.userDetailsApi.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Timing

data class UserDetailsResponseModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("wallet") val wallet: Int? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("highestQualification") val highestQualification: String? = null,
    @SerializedName("topic") val topic: ArrayList<String> = arrayListOf(),
    @SerializedName("userFollower") val userFollower: ArrayList<String> = arrayListOf(),
    @SerializedName("userFollowing") val userFollowing: ArrayList<String> = arrayListOf(),
    @SerializedName("userPost") val userPost: ArrayList<String> = arrayListOf(),
    @SerializedName("authType") val authType: String? = null,
    @SerializedName("libraries") val libraries: ArrayList<String> = arrayListOf(),
    @SerializedName("gyms") val gyms: ArrayList<String> = arrayListOf(),
    @SerializedName("address") val address: Address? = Address(),
    @SerializedName("bookings") val bookings: ArrayList<String> = arrayListOf(),
    @SerializedName("devices") val devices: ArrayList<String> = arrayListOf(),
    @SerializedName("sessions") val sessions: ArrayList<Sessions> = arrayListOf(),
    @SerializedName("notifications") val notifications: ArrayList<Notifications> = arrayListOf(),
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("__v") val v: Int? = null,
    @SerializedName("userName") val username: String? = null,
    @SerializedName("sex") val sex: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readString(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        arrayListOf<Sessions>().apply {
            parcel.readArrayList(Sessions::class.java.classLoader)
        },
        arrayListOf<Notifications>().apply {
            parcel.readArrayList(Notifications::class.java.classLoader)
        },
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(userId)
        parcel.writeString(contact)
        parcel.writeString(photo)
        parcel.writeValue(wallet)
        parcel.writeString(bio)
        parcel.writeString(highestQualification)
        parcel.writeStringList(topic)
        parcel.writeStringList(userFollower)
        parcel.writeStringList(userFollowing)
        parcel.writeStringList(userPost)
        parcel.writeString(authType)
        parcel.writeStringList(libraries)
        parcel.writeStringList(gyms)
        parcel.writeParcelable(address, flags)
        parcel.writeStringList(bookings)
        parcel.writeStringList(devices)
        parcel.writeList(sessions)
        parcel.writeList(notifications)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(v)
        parcel.writeString(username)
        parcel.writeString(sex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetailsResponseModel> {
        override fun createFromParcel(parcel: Parcel): UserDetailsResponseModel {
            return UserDetailsResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<UserDetailsResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}


data class Sessions(

    @SerializedName("libraryId") val libraryId: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("startTime") val startTime: String? = null,
    @SerializedName("endTime") val endTime: String? = null,
    @SerializedName("status") val status: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(libraryId)
        parcel.writeString(date)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sessions> {
        override fun createFromParcel(parcel: Parcel): Sessions {
            return Sessions(parcel)
        }

        override fun newArray(size: Int): Array<Sessions?> {
            return arrayOfNulls(size)
        }
    }

}

data class Address(
    @SerializedName("state") val state: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString(), parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(state)
        parcel.writeString(district)
        parcel.writeString(pincode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}


data class Notifications(
    @SerializedName("date") val date: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("message") val message: Message? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("_id") val id: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Message::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeParcelable(message, flags)
        parcel.writeString(status)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notifications> {
        override fun createFromParcel(parcel: Parcel): Notifications {
            return Notifications(parcel)
        }

        override fun newArray(size: Int): Array<Notifications?> {
            return arrayOfNulls(size)
        }
    }
}

data class Message(
    @SerializedName("message") val message: String? = null,
    @SerializedName("senderName") val senderName: String? = null,
    @SerializedName("senderDp") val senderDp: String? = null,
    @SerializedName("senderId") val senderId: String? = null,
    @SerializedName("reciverId") val receiverId: String? = null,
    @SerializedName("recvierDp") val receiverDp: String? = null,
    @SerializedName("reciverName") val receiverName: String? = null,
    @SerializedName("slot") val slot: Int? = null,
    @SerializedName("slotTime") val slotTime: Timing? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(Timing::class.java.classLoader),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(senderName)
        parcel.writeString(senderDp)
        parcel.writeString(senderId)
        parcel.writeString(receiverId)
        parcel.writeString(receiverDp)
        parcel.writeString(receiverName)
        parcel.writeValue(slot)
        parcel.writeParcelable(slotTime, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}
