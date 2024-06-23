package com.indiastudygroupadmin.bottom_nav_bar.gym.network

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.bottom_nav_bar.gym.model.GymDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GymDetailsNetworkService {

    @GET(AppUrlsEndpoint.GET_GYM)
    fun callAllGymDetails(
    ): Call<GymDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_GYM)
    fun callDistrictGymDetails(
        @Query("district") district: String?
    ): Call<GymDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_GYM + "{id}")
    fun callIdGymDetails(
        @Path("id") id: String?
    ): Call<GymIdDetailsResponseModel>
}