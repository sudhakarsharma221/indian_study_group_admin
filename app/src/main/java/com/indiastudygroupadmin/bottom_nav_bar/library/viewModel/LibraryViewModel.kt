package com.indiastudygroupadmin.bottom_nav_bar.library.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.library.repository.LibraryRepository

object LibraryDetailData {
    var libDetailsResponseModel: LibraryIdDetailsResponseModel? = null
}

class LibraryViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var allLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    var idLibraryResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    var pincodeLibraryReponse = MutableLiveData<LibraryDetailsResponseModel>()
    private val repository = LibraryRepository()

    init {
        this.allLibraryResponse = repository.allLibraryResponse
        this.idLibraryResponse = repository.idLibraryResponse
        this.pincodeLibraryReponse = repository.pincodeLibraryReponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callGetAllLibrary() {
        repository.getAllLibraryDetailsResponse()
    }

    fun callIdLibrary(id: String?) {
        repository.getIdDetailsResponse(id)
    }

    fun callPinCodeLibrary(pinCode: String?) {
        repository.getPincodeLibraryResponseDetailsResponse(pinCode)
    }

    fun setLibraryDetailsResponse(response: LibraryIdDetailsResponseModel) {
        LibraryDetailData.libDetailsResponseModel = response
    }

    fun getLibraryDetailsResponse(): LibraryIdDetailsResponseModel? {
        return LibraryDetailData.libDetailsResponseModel
    }


}