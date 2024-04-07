package com.indiastudygroupadmin.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserDetailsPutRequestBodyModel(

    @SerializedName("name") val name: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("address") val address: Address? = Address(),
    )

