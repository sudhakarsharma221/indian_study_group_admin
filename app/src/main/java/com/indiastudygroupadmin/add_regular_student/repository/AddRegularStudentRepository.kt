package com.indiastudygroupadmin.add_regular_student.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indiastudygroupadmin.add_regular_student.model.AddStudentRequestBody
import com.indiastudygroupadmin.add_regular_student.network.AddRegularStudentService
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.retrofitUtils.RetrofitUtilClass
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRegularStudentRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val addRegularStudentResponse = MutableLiveData<UserDetailsResponseModel>()


    fun callAddRegularStudentApi(
        addStudentRequestBody: AddStudentRequestBody?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(AddRegularStudentService::class.java)
        val call = client.callAddRegularStudentApi(addStudentRequestBody)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("addRegularStudentResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    addRegularStudentResponse.postValue(body!!)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("addRegularStudentResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("addRegularStudentResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}