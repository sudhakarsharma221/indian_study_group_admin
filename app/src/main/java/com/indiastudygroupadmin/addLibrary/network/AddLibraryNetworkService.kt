package com.indiastudygroupadmin.addLibrary.network

import com.indiastudygroupadmin.addLibrary.model.AddLibraryRequestModel
import com.indiastudygroupadmin.addLibrary.model.AddLibraryResponseModel
import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AddLibraryNetworkService {

    @POST(AppUrlsEndpoint.ADD_LIBRARY)
    fun callAddLibraryDetails(
        @Header("userid") userId: String?, @Body postRequestModel: AddLibraryRequestModel?
    ): Call<AddLibraryResponseModel>


}