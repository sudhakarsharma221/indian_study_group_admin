package com.indiastudygroupadmin.add_regular_student.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.add_regular_student.model.AddStudentRequestBody
import com.indiastudygroupadmin.add_regular_student.repository.AddRegularStudentRepository
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel

class AddRegularStudentViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var addRegularStudentResponse = MutableLiveData<UserDetailsResponseModel>()
    private val repository = AddRegularStudentRepository()

    init {
        this.addRegularStudentResponse = repository.addRegularStudentResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callAddRegularStudentApi(
        addStudentRequestBody: AddStudentRequestBody?
    ) {
        repository.callAddRegularStudentApi(addStudentRequestBody)
    }
}