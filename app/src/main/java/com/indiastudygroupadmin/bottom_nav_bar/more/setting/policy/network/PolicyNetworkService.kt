package com.indiastudygroupadmin.bottom_nav_bar.more.setting.policy.network

import com.indiastudygroupadmin.bottom_nav_bar.more.setting.policy.model.PolicyResponseData
import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import retrofit2.Call
import retrofit2.http.GET

interface PolicyNetworkService {
    @GET(AppUrlsEndpoint.GET_INFO)
    fun callGetPolicy(
    ): Call<PolicyResponseData>

}