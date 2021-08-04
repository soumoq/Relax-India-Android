package org.relaxindia.model.login
import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("otp") val otp : Int
)
