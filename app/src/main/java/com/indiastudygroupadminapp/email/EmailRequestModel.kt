package com.indiastudygroupadminapp.email

data class EmailRequestModel(
    val category: String, val from: From, val subject: String, val text: String, val to: List<To>
)

data class From(
    val email: String, val name: String
)

data class To(
    val email: String
)