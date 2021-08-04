package org.relaxindia.model.otp
import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("error") val error : Boolean,
    @SerializedName("data") val data : OtpData,
    @SerializedName("message") val message : String
)
