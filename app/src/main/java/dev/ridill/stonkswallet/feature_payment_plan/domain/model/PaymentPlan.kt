package dev.ridill.stonkswallet.feature_payment_plan.domain.model

import android.os.Parcelable
import dev.ridill.stonkswallet.core.util.DatePatterns
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.format
import dev.ridill.stonkswallet.core.util.toLocalDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentPlan(
    val id: Long,
    val name: String,
    val amount: String,
    val category: PaymentCategory,
    val createdDateMillis: Long,
    val repeatMonthsPeriod: Int?,
    val nextDueMillis: Long = createdDateMillis
) : Parcelable {

    val dueDateFormatted: String
        get() = nextDueMillis.toLocalDate().format(
            DatePatterns.DAY_SHORT_MONTH_NAME_YEAR
        )

    companion object {
        val DEFAULT
            get() = PaymentPlan(
                id = 0L,
                name = "",
                amount = "",
                category = PaymentCategory.MISC,
                createdDateMillis = DateUtil.currentEpochMillis(),
                repeatMonthsPeriod = 1,
            )

        fun buildExpenseNote(name: String, category: PaymentCategory): String = buildString {
            append(name)
            append(" ")
            append(category.label).append(" Bill")
        }
    }
}