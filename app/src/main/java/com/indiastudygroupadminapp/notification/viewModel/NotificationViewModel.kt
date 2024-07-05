package com.indiastudygroupadminapp.notification.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadminapp.notification.model.NotificationStatusChangeRequestModel
import com.indiastudygroupadminapp.notification.model.NotificationStatusChangeResponseModel
import com.indiastudygroupadminapp.notification.repository.NotificationRepository

class NotificationViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var notificationChangeResponse = MutableLiveData<NotificationStatusChangeResponseModel>()
    private val repository = NotificationRepository()

    init {
        this.notificationChangeResponse = repository.notificationChangeResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPostChangeNotificationStatus(
        userId: String?, notificationStatusChangeRequestModel: NotificationStatusChangeRequestModel
    ) {
        repository.notificationChangeResponse(userId, notificationStatusChangeRequestModel)
    }


}