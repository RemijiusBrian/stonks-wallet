package dev.ridill.stonkswallet.feature_expenses.data.local

import androidx.room.*
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.ExpenseWithTagRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT DISTINCT(strftime('%Y', dateMillis / 1000, 'unixepoch')) FROM ExpenseEntity ORDER BY dateMillis DESC")
    fun getDistinctYears(): Flow<List<Int>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExpenseEntity
        WHERE strftime('%m-%Y', dateMillis / 1000, 'unixepoch') = :date
        ORDER BY dateMillis DESC
        """
    )
    fun getExpensesForDate(date: String): Flow<List<ExpenseWithTagRelation>>

    @Query(
        """
        SELECT IFNULL(SUM(amount), 0.0)
        FROM ExpenseEntity
        WHERE strftime('%m-%Y', dateMillis / 1000, 'unixepoch') = :date
        """
    )
    fun getExpenditureForDate(date: String): Flow<Double>

    @Transaction
    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseWithTagRelation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseEntity: ExpenseEntity): Long

    @Query("DELETE FROM ExpenseEntity WHERE id = :id")
    suspend fun deleteById(id: Long)
}