package com.indiastudygroupadmin.pincode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.pincode.PincodeRepository
import com.indiastudygroupadmin.pincode.PincodeResponseModel

class PincodeViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var pincodeResponse = MutableLiveData<PincodeResponseModel>()
    private val repository = PincodeRepository()

    init {
        this.pincodeResponse = repository.pincodeResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPinCodeDetails(pincode: String?) {
        repository.getPincodeDetails(pincode)
    }
}