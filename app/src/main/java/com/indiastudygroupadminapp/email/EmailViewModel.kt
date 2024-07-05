package com.indiastudygroupadminapp.email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmailViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var emailResponse = MutableLiveData<EmailResponseModel>()
    private val repository = EmailRepository()

    init {
        this.emailResponse = repository.emailResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun postEmail(emailRequestModel: EmailRequestModel?) {
        repository.postEmail(emailRequestModel)
    }
}
