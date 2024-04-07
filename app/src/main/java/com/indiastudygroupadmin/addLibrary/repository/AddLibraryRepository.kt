package com.indiastudygroupadmin.addLibrary.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indiastudygroupadmin.addLibrary.model.AddLibraryRequestModel
import com.indiastudygroupadmin.addLibrary.model.AddLibraryResponseModel
import com.indiastudygroupadmin.addLibrary.network.AddLibraryNetworkService
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.retrofitUtils.RetrofitUtilClass
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.network.UserDetailsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLibraryRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val addLibraryResponse = MutableLiveData<AddLibraryResponseModel>()

    fun addLibraryResponse(userId: String?, postRequestModel: AddLibraryRequestModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(AddLibraryNetworkService::class.java)
        val call = client.callAddLibraryDetails(userId, postRequestModel)
        call.enqueue(object : Callback<AddLibraryResponseModel?> {
            override fun onResponse(
                call: Call<AddLibraryResponseModel?>, response: Response<AddLibraryResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("addLibraryResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    addLibraryResponse.postValue(body!!)
                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "addLibraryResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<AddLibraryResponseModel?>, t: Throwable) {
                Log.d("addLibraryResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}