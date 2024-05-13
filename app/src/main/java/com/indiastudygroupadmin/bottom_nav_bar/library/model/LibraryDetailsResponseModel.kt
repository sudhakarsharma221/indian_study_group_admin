package com.indiastudygroupadmin.bottom_nav_bar.library.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LibraryDetailsResponseModel(
    @SerializedName("libraryDetailsResponseModel") var libraryDetailsResponseModel: List<LibraryResponseItem>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(LibraryResponseItem.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(libraryDetailsResponseModel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryDetailsResponseModel> {
        override fun createFromParcel(parcel: Parcel): LibraryDetailsResponseModel {
            return LibraryDetailsResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<LibraryDetailsResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class LibraryResponseItem(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("userid") var userid: String? = null,
    @SerializedName("contact") var contact: String? = null,
    @SerializedName("photo") var photo: ArrayList<String>? = arrayListOf(),
    @SerializedName("seats") var seats: Int? = null,
    @SerializedName("vacantSeats") var vacantSeats: ArrayList<Int>? = arrayListOf(),
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("seatDetails") var seatDetails: ArrayList<SeatDetails> = arrayListOf(),
    @SerializedName("ammenities") var ammenities: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("createdByAgent") var createdByAgent: String? = null,
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
    @SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
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
        parcel.readParcelable(Pricing::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readString(),
        arrayListOf<Timing>().apply {
            parcel.readList(this, Timing::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(userid)
        parcel.writeString(contact)
        parcel.writeList(photo)
        parcel.writeValue(seats)
        parcel.writeList(vacantSeats)
        parcel.writeString(bio)
        parcel.writeList(seatDetails)
        parcel.writeList(ammenities)
        parcel.writeParcelable(pricing, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeString(createdByAgent)
        parcel.writeList(timing)
        parcel.writeList(upcomingBooking)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(v)
        parcel.writeString(qr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryResponseItem> {
        override fun createFromParcel(parcel: Parcel): LibraryResponseItem {
            return LibraryResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<LibraryResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}

data class SeatDetails(
    @SerializedName("seatNumber") var seatNumber: Int? = null,
    @SerializedName("isBooked") var isBooked: Boolean? = null,
    @SerializedName("bookedBy") var bookedBy: String? = null,
    @SerializedName("_id") var id: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(seatNumber)
        parcel.writeValue(isBooked)
        parcel.writeString(bookedBy)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SeatDetails> {
        override fun createFromParcel(parcel: Parcel): SeatDetails {
            return SeatDetails(parcel)
        }

        override fun newArray(size: Int): Array<SeatDetails?> {
            return arrayOfNulls(size)
        }
    }
}

data class Pricing(
    @SerializedName("daily") var daily: Int? = null,
    @SerializedName("monthly") var monthly: Int? = null,
    @SerializedName("weekly") var weekly: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(daily)
        parcel.writeValue(monthly)
        parcel.writeValue(weekly)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pricing> {
        override fun createFromParcel(parcel: Parcel): Pricing {
            return Pricing(parcel)
        }

        override fun newArray(size: Int): Array<Pricing?> {
            return arrayOfNulls(size)
        }
    }
}

data class Address(
    @SerializedName("street") val street: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("latitude") val latitude: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(street)
        parcel.writeString(pincode)
        parcel.writeString(district)
        parcel.writeString(state)
        parcel.writeString(longitude)
        parcel.writeString(latitude)
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

data class Timing(
    @SerializedName("from") var from: String? = null,
    @SerializedName("to") var to: String? = null,
    @SerializedName("days") var days: ArrayList<String> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString(), parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeStringList(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Timing> {
        override fun createFromParcel(parcel: Parcel): Timing {
            return Timing(parcel)
        }

        override fun newArray(size: Int): Array<Timing?> {
            return arrayOfNulls(size)
        }
    }
}
