package dev.ridill.stonkswallet.feature_payment_plan.data.local

import androidx.room.*
import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.local.relation.PaymentPlanRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentPlanDao {

    @Query("SELECT * FROM PaymentPlanEntity ORDER BY category ASC, description ASC")
    fun getPaymentPlansList(): Flow<List<PaymentPlanEntity>>

    @Transaction
    @Query(
        """
        SELECT bill.id as billId,
            bill.description as billName,
            bill.nextDueDateMillis as nextDueDateMillis,
            bill.amount as amount,
            bill.category as category,
            (SELECT exp.id
                FROM ExpenseEntity exp
                WHERE exp.paymentPlanId == bill.id AND strftime('%m-%Y', exp.dateMillis / 1000, 'unixepoch') = :monthAndYear
            ) as expenseId
        FROM PaymentPlanEntity bill
        WHERE strftime('%m-%Y', bill.nextDueDateMillis / 1000, 'unixepoch') == :monthAndYear
    """
    )
    fun getPaymentsForMonth(monthAndYear: String): Flow<List<PaymentPlanRelation>>

    @Query(
        """
        UPDATE PaymentPlanEntity
        SET nextDueDateMillis = strftime('%s', date(nextDueDateMillis / 1000, 'unixepoch', '+' || (repeatMonthsPeriod * :multiplier) || ' months')) * 1000
        WHERE id = :billId
    """
    )
    suspend fun incrementNextDueDateForPlan(billId: Long, multiplier: Int)

    @Query(
        """
        UPDATE PaymentPlanEntity
        SET nextDueDateMillis = strftime('%s', date(nextDueDateMillis / 1000, 'unixepoch', '-' || (repeatMonthsPeriod * :multiplier) || ' months')) * 1000
        WHERE id = :billId
    """
    )
    suspend fun decrementNextDueDateForPlan(billId: Long, multiplier: Int)

    @Transaction
    @Query(
        """
        SELECT *
        FROM PaymentPlanEntity bill
        WHERE (SELECT exp.id 
            FROM ExpenseEntity exp
            WHERE exp.paymentPlanId == bill.id AND
            strftime('%m-%Y', exp.dateMillis / 1000, 'unixepoch') = strftime('%m-%Y', :dateMillis / 1000, 'unixepoch')
        ) IS NULL
        AND strftime('%m-%Y', bill.nextDueDateMillis / 1000, 'unixepoch') = strftime('%m-%Y', :dateMillis / 1000, 'unixepoch')
        AND (julianday(date(bill.nextDueDateMillis / 1000, 'unixepoch')) - julianday(date(:dateMillis / 1000, 'unixepoch')) BETWEEN 0 AND :maxDayDifference)
    """
    )
    suspend fun getUpcomingPaymentPlans(
        dateMillis: Long,
        maxDayDifference: Int
    ): List<PaymentPlanEntity>

    @Query("SELECT * FROM PaymentPlanEntity WHERE id = :id")
    suspend fun getPaymentPlanById(id: Long): PaymentPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentPlanEntity: PaymentPlanEntity)

    @Query("DELETE FROM PaymentPlanEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(id) FROM PaymentPlanEntity")
    suspend fun getPaymentPlanCount(): Int
}