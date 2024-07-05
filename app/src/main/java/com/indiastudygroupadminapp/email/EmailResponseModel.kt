package com.indiastudygroupadminapp.email

import com.google.gson.annotations.SerializedName

data class EmailResponseModel(
    @SerializedName("message_ids") val messageIds: ArrayList<String>? = arrayListOf(),
    @SerializedName("success") val success: Boolean? = null
)