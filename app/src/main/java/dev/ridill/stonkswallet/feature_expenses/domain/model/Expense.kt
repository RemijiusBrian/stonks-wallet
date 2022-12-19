package dev.ridill.stonkswallet.feature_expenses.domain.model

import android.os.Parcelable
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.getDayWithSuffix
import dev.ridill.stonkswallet.core.util.toLocalDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val id: Long = 0L,
    val note: String,
    val amount: Double,
    val dateMillis: Long,
    val tag: Tag?,
    val paymentPlanId: Long?
) : Parcelable {
    val amountFormatted: String
        get() = TextUtil.formatAmountWithCurrency(amount)

    val dateFormatted: String
        get() = dateMillis.toLocalDate().getDayWithSuffix(longName = true)

    fun isPartOfPaymentPlan(): Boolean = paymentPlanId != null

    companion object {
        val DEFAULT
            get() = Expense(
                id = 0L,
                note = "",
                amount = 0.0,
                dateMillis = DateUtil.currentEpochMillis(),
                tag = null,
                paymentPlanId = null
            )
    }
}