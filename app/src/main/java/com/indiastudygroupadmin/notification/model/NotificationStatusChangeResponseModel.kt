package com.indiastudygroupadmin.notification.model

import com.google.gson.annotations.SerializedName
import com.indiastudygroupadmin.userDetailsApi.model.Notifications

data class NotificationStatusChangeResponseModel(

    @SerializedName("message") val message: String? = null,
    @SerializedName("updatedUser") val updatedUser: UpdatedUser? = null

)

data class UpdatedUser(

    @SerializedName("notifications") val notifications: ArrayList<Notifications>? = arrayListOf(),

    )
