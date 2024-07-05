package com.indiastudygroupadminapp.add_regular_student.network

import com.indiastudygroupadminapp.add_regular_student.model.AddStudentRequestBody
import com.indiastudygroupadminapp.app_utils.AppUrlsEndpoint
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AddRegularStudentService {
    @POST(AppUrlsEndpoint.ADD_REGULAR_STUDENT)
    fun callAddRegularStudentApi(
        @Body addStudentRequestBody: AddStudentRequestBody?
    ): Call<UserDetailsResponseModel>
}