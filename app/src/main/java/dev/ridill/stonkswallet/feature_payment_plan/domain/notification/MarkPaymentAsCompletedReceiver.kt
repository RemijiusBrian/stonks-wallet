package dev.ridill.stonkswallet.feature_payment_plan.domain.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.notification.NotificationHelper
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.toDoubleOrZero
import dev.ridill.stonkswallet.di.ApplicationScope
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toPaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkPaymentAsCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var paymentPlanRepository: PaymentPlanRepository

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    @ApplicationScope
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var notificationHelper: NotificationHelper<PaymentPlan>

    override fun onReceive(context: Context?, intent: Intent?) {
        val billId = intent?.getLongExtra(BILL_REMINDER_ID, -1L) ?: return

        if (billId != -1L) applicationScope.launch {
            val paymentPlan =
                paymentPlanRepository.getPlanById(billId)?.toPaymentPlan() ?: return@launch
            val expenseEntity = ExpenseEntity(
                note = PaymentPlan.buildExpenseNote(paymentPlan.name, paymentPlan.category),
                amount = paymentPlan.amount.toDoubleOrZero(),
                dateMillis = DateUtil.currentEpochMillis(),
                paymentPlanId = paymentPlan.id
            )
            expenseRepository.cacheExpense(expenseEntity)
            paymentPlanRepository.incrementNextDueDate(paymentPlan.id)
            notificationHelper.updateNotification(paymentPlan) {
                it.setContentText(context?.getString(R.string.payment_made).orEmpty())
            }
        }
    }
}