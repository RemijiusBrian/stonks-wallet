package dev.ridill.stonkswallet.feature_payment_plan.domain.payment_reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ridill.stonkswallet.core.notification.NotificationHelper
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toPaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanRepository
import kotlinx.coroutines.withContext

@HiltWorker
class PaymentPlanReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: PaymentPlanRepository,
    private val notificationHelper: NotificationHelper<PaymentPlan>,
    private val dispatcherProvider: DispatcherProvider
) : CoroutineWorker(appContext, params) {

    companion object {
        const val NAME = "PAYMENT_PLAN_REMINDER_WORKER"
    }

    override suspend fun doWork(): Result = withContext(dispatcherProvider.io) {
        try {
            val currentDate = DateUtil.currentEpochMillis()
            val upcomingPayments = repo.getUpcomingPlans(currentDate)
                .ifEmpty { return@withContext Result.success() }
                .map { it.toPaymentPlan() }

            notificationHelper.showNotification(*upcomingPayments.toTypedArray())
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}