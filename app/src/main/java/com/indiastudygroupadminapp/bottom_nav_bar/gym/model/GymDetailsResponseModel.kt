package com.indiastudygroupadminapp.bottom_nav_bar.gym.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Address
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.History
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Pricing
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Rating
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Reviews
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.SeatDetails
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Timing

data class GymDetailsResponseModel(
    @SerializedName("gymDetailsResponseModel") var gymDetailsResponseModel: List<GymResponseItem>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(GymResponseItem)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(gymDetailsResponseModel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GymDetailsResponseModel> {
        override fun createFromParcel(parcel: Parcel): GymDetailsResponseModel {
            return GymDetailsResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<GymDetailsResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class GymResponseItem(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("userid") var userid: String? = null,
    @SerializedName("ownerName") var ownerName: String? = null,
    @SerializedName("ownerPhoto") var ownerPhoto: String? = null,
    @SerializedName("contact") var contact: String? = null,
    @SerializedName("rating") var rating: Rating? = null,
    @SerializedName("reviews") var reviews: ArrayList<Reviews>? = arrayListOf(),
    @SerializedName("history") var history: ArrayList<History>? = arrayListOf(),
    @SerializedName("photo") var photo: ArrayList<String>? = arrayListOf(),
    @SerializedName("seats") var seats: Int? = null,
    @SerializedName("vacantSeats") var vacantSeats: ArrayList<Int>? = arrayListOf(),
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("seatDetails") var seatDetails: ArrayList<SeatDetails> = arrayListOf(),
    @SerializedName("ammenities") var ammenities: ArrayList<String> = arrayListOf(),
    @SerializedName("equipments") var equipments: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("createdByAgent") var createdByAgent: String? = null,
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
    @SerializedName("trainers") var trainers: ArrayList<Trainer> = arrayListOf(),
    //@SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null,
    @SerializedName("qr") var qr: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Rating::class.java.classLoader),
        arrayListOf<Reviews>().apply {
            parcel.readList(this, Reviews::class.java.classLoader)
        },
        arrayListOf<History>().apply {
            parcel.readList(this, History::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readValue(Int::class.java.classLoader) as? Int,
        arrayListOf<Int>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readString(),
        arrayListOf<SeatDetails>().apply {
            parcel.readList(this, SeatDetails::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readParcelable(Pricing::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readString(),
        arrayListOf<Timing>().apply {
            parcel.readList(this, Timing::class.java.classLoader)
        },
        arrayListOf<Trainer>().apply {
            parcel.readList(this, Trainer::class.java.classLoader)
        },
//        arrayListOf<String>().apply {
//            parcel.readList(this, String::class.java.classLoader)
//        },
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(userid)
        parcel.writeString(ownerName)
        parcel.writeString(ownerPhoto)
        parcel.writeString(contact)
        parcel.writeParcelable(rating, flags)
        parcel.writeList(reviews)
        parcel.writeList(history)
        parcel.writeList(photo)
        parcel.writeValue(seats)
        parcel.writeList(vacantSeats)
        parcel.writeString(bio)
        parcel.writeList(seatDetails)
        parcel.writeList(ammenities)
        parcel.writeList(equipments)
        parcel.writeParcelable(pricing, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeString(createdByAgent)
        parcel.writeList(timing)
        parcel.writeList(trainers)
//        parcel.writeList(upcomingBooking)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(v)
        parcel.writeString(qr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GymResponseItem> {
        override fun createFromParcel(parcel: Parcel): GymResponseItem {
            return GymResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<GymResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}

data class Trainer(

    @SerializedName("name") val name: String? = null,
    @SerializedName("certificate") val certificate: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(certificate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trainer> {
        override fun createFromParcel(parcel: Parcel): Trainer {
            return Trainer(parcel)
        }

        override fun newArray(size: Int): Array<Trainer?> {
            return arrayOfNulls(size)
        }
    }
}

