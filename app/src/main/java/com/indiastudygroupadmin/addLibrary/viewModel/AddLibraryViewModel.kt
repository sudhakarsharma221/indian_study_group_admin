package com.indiastudygroupadmin.addLibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.addLibrary.model.AddLibraryRequestModel
import com.indiastudygroupadmin.addLibrary.model.AddLibraryResponseModel
import com.indiastudygroupadmin.addLibrary.repository.AddLibraryRepository
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel

class AddLibraryViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var addLibraryResponse = MutableLiveData<AddLibraryResponseModel>()
    private val repository = AddLibraryRepository()

    init {
        this.addLibraryResponse = repository.addLibraryResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callAddLibrary(userId: String?, postRequestModel: AddLibraryRequestModel?) {
        repository.addLibraryResponse(userId, postRequestModel)
    }

}