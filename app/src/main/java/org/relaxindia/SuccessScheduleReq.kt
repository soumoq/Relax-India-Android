package org.relaxindia

data class SuccessScheduleReq(
    val driver_id: Int,
    val driver_name: String,
    val driver_phone: String,
    val driver_image: String,
    val driver_secondary_phone: String,
    val driver_avg_rating: Double,
    val from_location: String,
    val to_location: String,
    val schedule_date_time: String,
    val user_comment: String,
    val booking_amount: Double,
    val total_amount: Double,
    val status: String,
    val date: String,
    )

