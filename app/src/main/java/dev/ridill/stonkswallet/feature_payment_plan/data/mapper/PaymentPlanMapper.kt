package dev.ridill.stonkswallet.feature_payment_plan.data.mapper

import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.DatePatterns
import dev.ridill.stonkswallet.core.util.format
import dev.ridill.stonkswallet.core.util.toDoubleOrZero
import dev.ridill.stonkswallet.core.util.toLocalDate
import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.local.relation.PaymentPlanRelation
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.*

fun PaymentPlanEntity.toPaymentPlanListItem(): PaymentPlanListItem = PaymentPlanListItem(
    id = id,
    name = description,
    category = PaymentCategory.valueOf(category),
    amount = TextUtil.formatAmountWithCurrency(amount),
    dueDate = nextDueDateMillis.toLocalDate().format(DatePatterns.DAY_WITH_SHORT_MONTH_NAME)
)

fun PaymentPlanEntity.toPaymentPlan(): PaymentPlan = PaymentPlan(
    id = id,
    name = description,
    amount = amount.toString(),
    category = PaymentCategory.valueOf(category),
    createdDateMillis = creationDateMillis,
    repeatMonthsPeriod = repeatMonthsPeriod,
    nextDueMillis = nextDueDateMillis
)

fun PaymentPlanRelation.toPaymentPlanExpense(
    isDueAfter: (dueDateMillis: Long) -> Boolean
): PaymentPlanExpense = PaymentPlanExpense(
    billId = billId,
    amount = amount,
    name = billName,
    dueDateMillis = nextDueDateMillis,
    category = PaymentCategory.valueOf(category),
    status = when {
        expenseId != null -> PaymentStatus.PAID
        isDueAfter(nextDueDateMillis) -> PaymentStatus.UPCOMING
        else -> PaymentStatus.UNPAID
    }
)

fun PaymentPlan.toEntity(): PaymentPlanEntity = PaymentPlanEntity(
    id = id,
    description = name,
    category = category.name,
    creationDateMillis = createdDateMillis,
    amount = amount.toDoubleOrZero(),
    repeatMonthsPeriod = repeatMonthsPeriod,
    nextDueDateMillis = nextDueMillis
)