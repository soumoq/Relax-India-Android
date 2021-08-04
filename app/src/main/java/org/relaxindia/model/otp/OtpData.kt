package org.relaxindia.model.otp
import com.google.gson.annotations.SerializedName

data class OtpData(
    @SerializedName("access_token") val access_token : String,
    @SerializedName("profile_completion_status") val profile_completion_status : String,

)
