package org.relaxindia.model.userProfile

import org.relaxindia.model.userProfile.*
import com.google.gson.annotations.SerializedName


data class ProfileResponse(
    @SerializedName("error") val error : Boolean,
    @SerializedName("data") val data : ProfileData,
    @SerializedName("message") val message : String
)