package com.indiastudygroupadminapp.notification.model

import com.google.gson.annotations.SerializedName

data class NotificationStatusChangeRequestModel(
    @SerializedName("notificationId") val notificationId: String? = null
)
