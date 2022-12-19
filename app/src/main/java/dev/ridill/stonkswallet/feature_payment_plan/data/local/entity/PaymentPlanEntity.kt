package dev.ridill.stonkswallet.feature_payment_plan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PaymentPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val description: String,
    val amount: Double,
    val creationDateMillis: Long,
    val category: String,
    val repeatMonthsPeriod: Int?,
    val nextDueDateMillis: Long
)