package com.indiastudygroupadmin.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserExistResponseModel(
    @SerializedName("userExist") val userExist: Boolean? = null,
    @SerializedName("user") val user: User? = null,
)

data class User(
    @SerializedName("authType") val authType: String? = null,
)