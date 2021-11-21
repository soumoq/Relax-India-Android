package org.relaxindia.model

data class ScheduleReq(
    val booking_id: Int,
    val booking_amount: Double,
    val total_amount: Double,
    val from_location: String,
    val to_location: String,
    val schedule_date_time: String,
    val user_comment: String,
    val status: String
)
