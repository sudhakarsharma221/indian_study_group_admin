package com.indiastudygroupadmin.add_regular_student.model

import com.google.gson.annotations.SerializedName

data class AddStudentRequestBody(
    @SerializedName("contact") val contact: Long? = null,
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("numberOfDays") val numberOfDays: String? = null,
    @SerializedName("startTimeHour") val startTimeHour: String? = null,
    @SerializedName("startTimeMinute") val startTimeMinute: String? = null,
    @SerializedName("endTimeHour") val endTimeHour: String? = null,
    @SerializedName("endTimeMinute") val endTimeMinute: String? = null,

    )
