package com.indiastudygroupadmin.email

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.pincode.PincodeResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface EmailNetworkService {

    @POST(AppUrlsEndpoint.SEND_EMAIL)
    @Headers(
        "Authorization: Bearer 86e8f9a8a5144d0d9c82b99a892a5da6", "Content-Type: application/json"
    )
    fun callEmailApi(
        @Body emailRequestModel: EmailRequestModel?
    ): Call<EmailResponseModel>
}