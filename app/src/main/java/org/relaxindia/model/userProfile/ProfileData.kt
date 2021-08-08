package org.relaxindia.model.userProfile

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("pincode") val pincode: String
)
