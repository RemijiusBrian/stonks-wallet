package dev.ridill.stonkswallet.feature_payment_plan.domain.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.application.SWActivity
import dev.ridill.stonkswallet.core.notification.NotificationHelper
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan

class PaymentPlanNotificationHelper(
    private val applicationContext: Context
) : NotificationHelper<PaymentPlan> {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val pendingIntentFlags =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private val openBillsIntent = Intent(
        Intent.ACTION_VIEW,
        "https://www.xpensetracker.ridill.dev/bills_list".toUri(),
        applicationContext,
        SWActivity::class.java
    )
    private val openBillsPendingIntent =
        PendingIntent.getActivity(applicationContext, 0, openBillsIntent, pendingIntentFlags)

    init {
        createNotificationChannel()
    }

    override fun createNotificationChannel() {
        val channelGroup = NotificationChannelGroupCompat
            .Builder(NotificationHelper.ChannelGroups.BILLS)
            .setName(applicationContext.getString(R.string.notification_channel_group_payment_plans))
            .setDescription(applicationContext.getString(R.string.notification_channel_group_bills_desc))
            .build()
        notificationManager.createNotificationChannelGroupsCompat(listOf(channelGroup))

        val billReminderChannel = NotificationChannelCompat.Builder(
            BILL_REMINDER_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setGroup(NotificationHelper.ChannelGroups.BILLS)
            .setName(applicationContext.getString(R.string.notification_channel_payment_plan_reminder))
            .build()

        notificationManager.createNotificationChannel(billReminderChannel)
    }

    override fun getBaseNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, BILL_REMINDER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openBillsPendingIntent)
            .setAutoCancel(true)
    }

    override fun showNotification(vararg data: PaymentPlan) {
        if (data.isEmpty()) return
        if (!notificationManager.areNotificationsEnabled()) return
        data.forEach { bill ->
            val notification = getBaseNotification()
                .setContentTitle(
                    "${bill.name} (${bill.category.label})"
                )
                .setContentText(
                    applicationContext.getString(
                        R.string.upcoming_payment_reminder_notification_text,
                        bill.dueDateFormatted
                    )
                )
                .addAction(
                    R.drawable.ic_notification,
                    applicationContext.getString(R.string.mark_as_paid),
                    buildActionPendingIntent(bill)
                )
            notificationManager.notify(bill.id.toInt(), notification.build())
        }
        if (data.size > 1) {
            val summary = buildSummaryNotification().build()
            notificationManager.notify(BILL_SUMMARY_ID, summary)
        }
    }

    override fun updateNotification(
        data: PaymentPlan,
        update: (updateNotification: NotificationCompat.Builder) -> NotificationCompat.Builder
    ) {
        val updateNotification = getBaseNotification()
            .setContentTitle(
                "${data.name} (${data.category.label})"
            )
        update(updateNotification)
        notificationManager.notify(data.id.toInt(), updateNotification.build())
    }

    override fun dismissNotification(id: Int) {
        notificationManager.cancel(id)
    }

    override fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }

    private fun buildActionPendingIntent(data: PaymentPlan): PendingIntent {
        val actionIntent =
            Intent(applicationContext, MarkPaymentAsCompletedReceiver::class.java).apply {
                putExtra(BILL_REMINDER_ID, data.id)
            }
        return PendingIntent.getBroadcast(
            applicationContext,
            data.id.toInt(),
            actionIntent,
            pendingIntentFlags
        )
    }

    private fun buildSummaryNotification(): NotificationCompat.Builder = getBaseNotification()
        .setStyle(
            NotificationCompat.InboxStyle()
                .setSummaryText(applicationContext.getString(R.string.upcoming_payments))
        )
        .setGroupSummary(true)
}

const val BILL_REMINDER_ID = "billId"
private const val BILL_REMINDER_NOTIFICATION_CHANNEL_ID = "bill_reminder_notification_channel"
private const val BILL_SUMMARY_ID = 3