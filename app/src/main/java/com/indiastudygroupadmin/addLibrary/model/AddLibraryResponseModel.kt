package com.indiastudygroupadmin.addLibrary.model

import com.google.gson.annotations.SerializedName

data class AddLibraryResponseModel(
    @SerializedName("name") val name: String? = null,
    @SerializedName("userid") val userid: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("seats") val seats: Int? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("ammenities") val amenities: ArrayList<String> = arrayListOf(),
    @SerializedName("address") val address: Address = Address()
)

