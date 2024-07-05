package com.indiastudygroupadminapp.notification.network

import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import com.indiastudygroupadminapp.notification.model.NotificationStatusChangeRequestModel
import com.indiastudygroupadminapp.notification.model.NotificationStatusChangeResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationNetworkService {
    @POST(AppUrlsEndpoint.NOTIFICATION_STATUS)
    fun callNotificationChangeStatus(
        @Header("userid") userId: String?,
        @Body notificationStatusChangeRequestModel: NotificationStatusChangeRequestModel
    ): Call<NotificationStatusChangeResponseModel>

}