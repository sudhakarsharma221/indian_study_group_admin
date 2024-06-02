package com.indiastudygroupadmin.notification.network

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.notification.model.NotificationStatusChangeRequestModel
import com.indiastudygroupadmin.notification.model.NotificationStatusChangeResponseModel
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