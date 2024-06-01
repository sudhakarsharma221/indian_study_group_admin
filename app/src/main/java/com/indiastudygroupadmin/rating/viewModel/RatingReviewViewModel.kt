package com.indiastudygroupadmin.rating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadmin.rating.model.RatingRequestModel
import com.indiastudygroupadmin.rating.model.ReviewRequestModel
import com.indiastudygroupadmin.rating.repository.RatingReviewRepository

class RatingReviewViewModel : ViewModel() {


    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var ratingResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    var reviewResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    private val repository = RatingReviewRepository()

    init {
        this.ratingResponse = repository.ratingResponse
        this.reviewResponse = repository.reviewResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun postReview(userId: String?, reviewRequestModel: ReviewRequestModel) {
        repository.postReview(userId, reviewRequestModel)
    }

    fun postRating(userId: String?, ratingRequestModel: RatingRequestModel?) {
        repository.postRating(userId, ratingRequestModel)
    }

}