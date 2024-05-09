package com.indiastudygroupadmin.addLibrary.model

import com.google.gson.annotations.SerializedName

data class AddLibraryRequestModel(
    @SerializedName("name") val name: String? = null,
    @SerializedName("userid") val userid: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("seats") val seats: Int? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("ammenities") val amenities: ArrayList<String>? = arrayListOf(),
    @SerializedName("address") val address: Address = Address(),
    @SerializedName("pricing") val pricing: Pricing = Pricing(),
    @SerializedName("timing") val timing: ArrayList<AddTimingsDataClass> = arrayListOf()
)

data class Address(
    @SerializedName("street") val street: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("latitude") val latitude: String? = null
)

data class Pricing(
    @SerializedName("daily") val daily: Int? = null,
    @SerializedName("monthly") val monthly: Int? = null,
    @SerializedName("weekly") val weekly: Int? = null
)
data class AddTimingsDataClass(
    @SerializedName("days") val days: ArrayList<String>? = arrayListOf(),
    @SerializedName("from") val from: String? = null,
    @SerializedName("to") val to: String? = null
)
