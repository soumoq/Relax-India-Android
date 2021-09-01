package org.relaxindia.model.saveBooking

import com.google.gson.annotations.SerializedName

data class SaveBooking(
    @SerializedName("error") val error: Boolean,
    @SerializedName("data") val data: SaveBookingData,
    @SerializedName("message") val message: String
)
