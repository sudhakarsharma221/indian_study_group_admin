package com.indiastudygroupadminapp.bottom_nav_bar.more.setting.policy.network

import com.indiastudygroupadminapp.bottom_nav_bar.more.setting.policy.model.PolicyResponseData
import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import retrofit2.Call
import retrofit2.http.GET

interface PolicyNetworkService {
    @GET(AppUrlsEndpoint.GET_INFO)
    fun callGetPolicy(
    ): Call<PolicyResponseData>

}