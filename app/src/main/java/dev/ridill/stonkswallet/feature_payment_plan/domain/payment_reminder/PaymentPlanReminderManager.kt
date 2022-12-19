package dev.ridill.stonkswallet.feature_payment_plan.domain.payment_reminder

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PaymentPlanReminderManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleReminder() {
        val workRequest = PeriodicWorkRequestBuilder<PaymentPlanReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            PaymentPlanReminderWorker.NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopReminder() {
        workManager.cancelUniqueWork(PaymentPlanReminderWorker.NAME)
    }
}