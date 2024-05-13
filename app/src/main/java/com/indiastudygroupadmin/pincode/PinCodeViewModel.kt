package com.indiastudygroupadmin.pincode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PinCodeViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var pinCodeResponse = MutableLiveData<PincodeResponseModel>()
    private val repository = PinCodeRepository()

    init {
        this.pinCodeResponse = repository.pincodeResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPinCodeDetails(pincode: String?) {
        repository.getPincodeDetails(pincode)
    }
}