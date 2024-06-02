package com.indiastudygroupadmin.add_regular_student.model

import com.google.gson.annotations.SerializedName

data class AddStudentRequestBody(
    @SerializedName("contact") val contact: Long? = null,
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("numberOfDays") val numberOfDays: Int? = null,
    @SerializedName("startTimeHour") val startTimeHour: Int? = null,
    @SerializedName("startTimeMinute") val startTimeMinute: Int? = null,
    @SerializedName("endTimeHour") val endTimeHour: Int? = null,
    @SerializedName("endTimeMinute") val endTimeMinute: Int? = null,


    )
