package com.indiastudygroupadmin.add_regular_student.network

import com.indiastudygroupadmin.add_regular_student.model.AddStudentRequestBody
import com.indiastudygroupadmin.app_utils.AppUrlsEndpoint
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AddRegularStudentService {
    @POST(AppUrlsEndpoint.ADD_REGULAR_STUDENT)
    fun callAddRegularStudentApi(
        @Body addStudentRequestBody: AddStudentRequestBody?
    ): Call<UserDetailsResponseModel>
}