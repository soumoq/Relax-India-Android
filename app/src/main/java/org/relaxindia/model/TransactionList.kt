package org.relaxindia.model

data class TransactionList(
    val booking_id: Int,
    val amount_paid: String,
    val journey_total_amount: String,
    val status: String,
    val booking_type: String,
    val date: String,
    )
