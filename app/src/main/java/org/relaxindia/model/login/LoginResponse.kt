package org.relaxindia.model.login
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error") val error : Boolean,
    @SerializedName("data") val data : LoginData,
    @SerializedName("message") val message : String
)
