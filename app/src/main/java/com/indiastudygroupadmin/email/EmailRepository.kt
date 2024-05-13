package com.indiastudygroupadmin.email

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val emailResponse = MutableLiveData<EmailResponseModel>()

    fun postEmail(emailRequestModel: EmailRequestModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofitEmail().create(EmailNetworkService::class.java)
        val call = client.callEmailApi(emailRequestModel)
        call.enqueue(object : Callback<EmailResponseModel?> {
            override fun onResponse(
                call: Call<EmailResponseModel?>, response: Response<EmailResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("emailResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    emailResponse.postValue(body!!)
                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<EmailResponseModel?>, t: Throwable) {
                Log.d("emailResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}