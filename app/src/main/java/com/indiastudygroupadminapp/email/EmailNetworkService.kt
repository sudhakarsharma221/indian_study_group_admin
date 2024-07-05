package com.indiastudygroupadminapp.email

import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmailNetworkService {

    @POST(AppUrlsEndpoint.SEND_EMAIL)
    @Headers(
        "Authorization: Bearer 86e8f9a8a5144d0d9c82b99a892a5da6", "Content-Type: application/json"
    )
    fun callEmailApi(
        @Body emailRequestModel: EmailRequestModel?
    ): Call<EmailResponseModel>
}