package com.indiastudygroupadmin.userDetailsApi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.model.UserExistResponseModel
import com.indiastudygroupadmin.userDetailsApi.repository.UserDetailsRepository

object UserDetailData {
    var userDetailsResponseModel: UserDetailsResponseModel? = null
}

class UserDetailsViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var userDetailsResponse = MutableLiveData<UserDetailsResponseModel>()
    var userExistResponse = MutableLiveData<UserExistResponseModel>()
    private val repository = UserDetailsRepository()

    init {
        this.userDetailsResponse = repository.userDetailsResponse
        this.userExistResponse = repository.userExistResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callGetUserDetails(userId: String?) {
        repository.getUserDetailsResponse(userId)
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