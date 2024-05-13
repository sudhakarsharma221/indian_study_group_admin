package com.indiastudygroupadmin.email

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.pincode.PincodeResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface EmailNetworkService {

    @GET(AppUrlsEndpoint.SEND_EMAIL)
    fun callEmailApi(
        @Body emailRequestModel: EmailRequestModel?
    ): Call<EmailResponseModel>
}