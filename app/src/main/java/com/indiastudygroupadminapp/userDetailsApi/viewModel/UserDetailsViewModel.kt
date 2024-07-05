package com.indiastudygroupadminapp.userDetailsApi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadminapp.userDetailsApi.model.AddFcmResponseModel
import com.indiastudygroupadminapp.userDetailsApi.model.AddFcmTokenRequestBody
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadminapp.userDetailsApi.model.UserExistResponseModel
import com.indiastudygroupadminapp.userDetailsApi.repository.UserDetailsRepository

object UserDetailData {
    var userDetailsResponseModel: UserDetailsResponseModel? = null
}

class UserDetailsViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var addFcmTokenResponse = MutableLiveData<AddFcmResponseModel>()
    var userDetailsResponse = MutableLiveData<UserDetailsResponseModel>()
    var userDetailsIdResponse = MutableLiveData<UserDetailsResponseModel>()
    var userExistResponse = MutableLiveData<UserExistResponseModel>()
    private val repository = UserDetailsRepository()

    init {
        this.userDetailsResponse = repository.userDetailsResponse
        this.userExistResponse = repository.userExistResponse
        this.addFcmTokenResponse = repository.addFcmTokenResponse
        this.userDetailsIdResponse = repository.userDetailsIdResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPostFcmToken(userId: String?, addFcmTokenRequestBody: AddFcmTokenRequestBody) {
        repository.postFcmToken(userId, addFcmTokenRequestBody)
    }

    fun callGetUserDetails(userId: String?) {
        repository.getUserDetailsResponse(userId)
    }

    fun callGetUserIdDetails(id: String?) {
        repository.getUserDetailsIdResponse(id)
    }

    fun callPostUserDetails(postUserDetailsPostRequestBodyModel: UserDetailsPostRequestBodyModel?) {
        repository.postUserDetailsResponse(postUserDetailsPostRequestBodyModel)
    }

    fun callPutUserDetails(
        userId: String?, putUserDetailsPostRequestBodyModel: UserDetailsPutRequestBodyModel?
    ) {
        repository.putUserDetailsResponse(userId, putUserDetailsPostRequestBodyModel)
    }

    fun callUserExists(contact: String?, userName: String?) {
        repository.getUserExist(contact, userName)
    }

    fun setUserDetailsResponse(response: UserDetailsResponseModel) {
        UserDetailData.userDetailsResponseModel = response
    }

    fun getUserDetailsResponse(): UserDetailsResponseModel? {
        return UserDetailData.userDetailsResponseModel
    }

}