package com.indiastudygroupadminapp.bottom_nav_bar.library.network

import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryDetailsResponseModel
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LibraryDetailsNetworkService {


    @GET(AppUrlsEndpoint.GET_LIBRARY)
    fun callAllLibraryDetails(
    ): Call<LibraryDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_LIBRARY)
    fun callPincodeLibraryDetails(
        @Query("pincode") pincode: String?
    ): Call<LibraryDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_LIBRARY + "{id}")
    fun callIdLibraryDetails(
        @Path("id") id: String?
    ): Call<LibraryIdDetailsResponseModel>

}