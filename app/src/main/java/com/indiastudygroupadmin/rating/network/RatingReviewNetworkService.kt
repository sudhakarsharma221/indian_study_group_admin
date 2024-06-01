package com.indiastudygroupadmin.rating.network

import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadmin.rating.model.RatingRequestModel
import com.indiastudygroupadmin.rating.model.ReviewRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RatingReviewNetworkService {

    @POST(AppUrlsEndpoint.POST_REVIEW)
    fun postReview(

        @Header("userid") userId: String?, @Body reviewRequestModel: ReviewRequestModel?
    ): Call<LibraryIdDetailsResponseModel>

    @POST(AppUrlsEndpoint.POST_RATING)
    fun postRating(

        @Header("userid") userId: String?, @Body ratingRequestModel: RatingRequestModel?
    ): Call<LibraryIdDetailsResponseModel>
}