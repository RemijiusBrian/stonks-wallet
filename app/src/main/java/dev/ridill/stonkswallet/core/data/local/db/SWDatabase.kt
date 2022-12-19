package dev.ridill.stonkswallet.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.ridill.stonkswallet.feature_expenses.data.local.ExpenseDao
import dev.ridill.stonkswallet.feature_expenses.data.local.TagsDao
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.local.PaymentPlanDao
import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity

@Database(
    entities = [
        ExpenseEntity::class,
        TagEntity::class,
        PaymentPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SWDatabase : RoomDatabase() {

    companion object {
        const val NAME = "StonksWallet.db"
    }

    // Dao
    abstract val expenseDao: ExpenseDao
    abstract val tagsDao: TagsDao
    abstract val paymentPlanDao: PaymentPlanDao
}