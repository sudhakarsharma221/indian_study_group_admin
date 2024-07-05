package com.indiastudygroupadminapp.rating.network

import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadminapp.rating.model.RatingRequestModel
import com.indiastudygroupadminapp.rating.model.ReviewRequestModel
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