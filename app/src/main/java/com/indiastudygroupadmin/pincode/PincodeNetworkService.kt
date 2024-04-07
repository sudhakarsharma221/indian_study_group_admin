package com.indiastudygroupadmin.pincode

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PincodeNetworkService {
    @GET(AppUrlsEndpoint.PINCODE_DETAILS)
    fun callPincodeDetailsApi(
        @Path("pinCode") pinCode: String?
    ): Call<PincodeResponseModel>
}
