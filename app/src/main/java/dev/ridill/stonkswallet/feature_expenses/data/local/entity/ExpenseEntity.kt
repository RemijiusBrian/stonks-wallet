package dev.ridill.stonkswallet.feature_expenses.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tagName"],
            childColumns = ["tag"]
        ),
        ForeignKey(
            entity = PaymentPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["paymentPlanId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tag"), Index("paymentPlanId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val note: String,
    val amount: Double,
    val dateMillis: Long,
    val tag: String? = null,
    val paymentPlanId: Long? = null
)