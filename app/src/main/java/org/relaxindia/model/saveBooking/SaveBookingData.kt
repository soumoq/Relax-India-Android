package org.relaxindia.model.saveBooking
import com.google.gson.annotations.SerializedName

data class SaveBookingData(
    @SerializedName("booking_id") val booking_id : String,

)
