package org.relaxindia.model.driverList

import com.google.gson.annotations.SerializedName
import org.relaxindia.model.driverList.DriverListInfo


data class DriverList(
    @SerializedName("error") val error: Boolean,
    @SerializedName("data") val data: List<DriverListInfo>,
    @SerializedName("message") val message: String
)