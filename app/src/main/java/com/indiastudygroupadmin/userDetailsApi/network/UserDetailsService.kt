package com.indiastudygroupadmin.userDetailsApi.network

import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.model.UserExistResponseModel
import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.userDetailsApi.model.AddFcmResponseModel
import com.indiastudygroupadmin.userDetailsApi.model.AddFcmTokenRequestBody
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserDetailsService {
    @GET(AppUrlsEndpoint.USERS_DETAILS)
    fun callGetUserDetails(
        @Header("userid") userId: String?
    ): Call<UserDetailsResponseModel>


    @GET(AppUrlsEndpoint.USERS_DETAILSID + "/{id}")
    fun callGetUserDetailsById(
        @Path("id") id: String?
    ): Call<UserDetailsResponseModel>

    @POST(AppUrlsEndpoint.USERS_DETAILS)
    fun callPostUserDetails(
        @Body postRequestModel: UserDetailsPostRequestBodyModel?
    ): Call<UserDetailsResponseModel>

    @PUT(AppUrlsEndpoint.USERS_DETAILS)
    fun callPutUserDetails(
        @Header("userid") userId: String?, @Body putRequestModel: UserDetailsPutRequestBodyModel?
    ): Call<UserDetailsResponseModel>

    @GET("api/users/{contact_number}")
    fun callGetUserExist(
        @Path("contact_number") phoneNo: String?, @Query("userName") userName: String?
    ): Call<UserExistResponseModel>

    @POST(AppUrlsEndpoint.ADD_FCM_TOKEN)
    fun callAddFcmToken(
        @Header("userid") userId: String?, @Body addFcmTokenRequestBody: AddFcmTokenRequestBody?
    ): Call<AddFcmResponseModel>

}