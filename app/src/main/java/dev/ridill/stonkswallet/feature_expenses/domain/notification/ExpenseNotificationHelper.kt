package dev.ridill.stonkswallet.feature_expenses.domain.notification

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
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense

class ExpenseNotificationHelper(
    private val applicationContext: Context
) : NotificationHelper<Expense> {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val pendingIntentFlags =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private fun buildPendingIntentWithId(id: Long): PendingIntent {
        val openExpensesIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.xpensetracker.ridill.dev/add_edit_expense/$id".toUri(),
            applicationContext,
            SWActivity::class.java
        )
        return PendingIntent.getActivity(
            applicationContext,
            0,
            openExpensesIntent,
            pendingIntentFlags
        )
    }

    init {
        createNotificationChannel()
    }

    override fun createNotificationChannel() {
        val channelGroup = NotificationChannelGroupCompat
            .Builder(NotificationHelper.ChannelGroups.EXPENSES)
            .setName(applicationContext.getString(R.string.notification_channel_group_expenses))
            .setDescription(applicationContext.getString(R.string.notification_channel_group_expenses_desc))
            .build()
        notificationManager.createNotificationChannelGroupsCompat(listOf(channelGroup))

        val billReminderChannel = NotificationChannelCompat.Builder(
            EXPENSE_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setGroup(NotificationHelper.ChannelGroups.EXPENSES)
            .setName(applicationContext.getString(R.string.notification_channel_expenses_added))
            .build()

        notificationManager.createNotificationChannelsCompat(listOf(billReminderChannel))
    }

    override fun getBaseNotification(): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, EXPENSE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.expense_notification_title))
            .setGroup(EXPENSE_NOTIFICATION_GROUP)

    override fun showNotification(vararg data: Expense) {
        if (data.isEmpty()) return
        if (!notificationManager.areNotificationsEnabled()) return
        data.forEach { expense ->
            val notification = getBaseNotification()
                .setContentText(
                    applicationContext.getString(
                        R.string.expense_notification_text,
                        expense.note
                    )
                )
                .setContentIntent(buildPendingIntentWithId(expense.id))
                .build()
            notificationManager.notify(expense.id.toInt(), notification)
        }
        if (data.size > 1) {
            val summary = buildSummaryNotification(data.size).build()
            notificationManager.notify(EXPENSE_SUMMARY_ID, summary)
        }
    }

    override fun dismissNotification(id: Int) {
        notificationManager.cancel(EXPENSE_SUMMARY_ID)
    }

    override fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }

    private fun buildSummaryNotification(expenseCount: Int): NotificationCompat.Builder =
        getBaseNotification()
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(applicationContext.getString(R.string.expenses_added_notification_summary))
                    .setSummaryText(
                        applicationContext.getString(
                            R.string.expenses_added_notification_summary,
                            expenseCount
                        )
                    )
            )
            .setGroupSummary(true)

}

private const val EXPENSE_NOTIFICATION_CHANNEL_ID = "expense_notification_channel"
private const val EXPENSE_NOTIFICATION_GROUP = "expense_group"
private const val EXPENSE_SUMMARY_ID = 2