package dev.ridill.stonkswallet.feature_payment_plan.domain.model

import android.os.Parcelable
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.DatePatterns
import dev.ridill.stonkswallet.core.util.format
import dev.ridill.stonkswallet.core.util.toLocalDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentPlanExpense(
    val billId: Long,
    val amount: Double,
    val name: String,
    val dueDateMillis: Long,
    val category: PaymentCategory,
    val status: PaymentStatus,
) : Parcelable {
    val amountFormatted: String
        get() = TextUtil.formatAmountWithCurrency(amount)

    val dateFormatted: String
        get() = dueDateMillis.toLocalDate().format(DatePatterns.DAY_WITH_SHORT_MONTH_NAME)
}