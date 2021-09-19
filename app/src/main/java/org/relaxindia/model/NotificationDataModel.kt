package org.relaxindia.model

import com.google.gson.annotations.SerializedName

data class NotificationDataModel(
    val bookingId: String,
    val sourceLoc: String,
    val desLoc: String,
    val amount: String,
    val deviceId : String

)
