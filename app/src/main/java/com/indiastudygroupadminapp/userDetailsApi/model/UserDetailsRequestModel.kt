package com.indiastudygroupadminapp.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserDetailsRequestModel(
    @SerializedName("_id")
    val id: String? = null
)