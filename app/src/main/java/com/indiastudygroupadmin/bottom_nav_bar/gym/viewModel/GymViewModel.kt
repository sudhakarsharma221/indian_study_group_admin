package com.indiastudygroupadmin.bottom_nav_bar.gym.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indiastudygroupadmin.bottom_nav_bar.gym.model.GymDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.gym.repository.GymDetailsRepository

object GymDetailData {
    var gymDetailsResponseModel: GymIdDetailsResponseModel? = null
}

class GymViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var showProgressAll = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var allGymResponse = MutableLiveData<GymDetailsResponseModel>()
    var idGymResponse = MutableLiveData<GymIdDetailsResponseModel>()
    var districtGymResponse = MutableLiveData<GymDetailsResponseModel>()
    private val repository = GymDetailsRepository()

    init {
        this.allGymResponse = repository.allGymResponse
        this.idGymResponse = repository.idGymResponse
        this.districtGymResponse = repository.districtGymResponse
        this.showProgress = repository.showProgress
        this.showProgressAll = repository.showProgressAll
        this.errorMessage = repository.errorMessage
    }

    fun callGetAllGym() {
        repository.getAllGymDetailsResponse()
    }

    fun callIdGym(id: String?) {
        repository.getGymIdDetailsResponse(id)
    }

    fun callDistrictGym(district: String?) {
        repository.getDistrictGymResponseDetailsResponse(district)
    }

    fun setGymDetailsResponse(response: GymIdDetailsResponseModel) {
        GymDetailData.gymDetailsResponseModel = response
    }

    fun getGymDetailsResponse(): GymIdDetailsResponseModel? {
        return GymDetailData.gymDetailsResponseModel
    }


}