package dev.ridill.stonkswallet.feature_expenses.data.local

import androidx.room.*
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.TagWithExpenditureRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {

    @Query("SELECT * FROM TagEntity ORDER BY tagName ASC")
    fun getTagsList(): Flow<List<TagEntity>>

    @Query(
        """
        SELECT tag.tagName as tag, tag.colorCode as colorCode,
            (SELECT SUM(amount)
            FROM ExpenseEntity subExp
            WHERE subExp.tag = tag.tagName AND strftime('%m-%Y', exp.dateMillis / 1000, 'unixepoch') = :date) as expenditure
        FROM TagEntity tag
        LEFT OUTER JOIN ExpenseEntity exp ON tag.tagName = exp.tag
        GROUP BY tag.tagName
        ORDER BY expenditure DESC, tag.tagName ASC
    """
    )
    fun getTagWithExpendituresForDate(date: String): Flow<List<TagWithExpenditureRelation>>

    @Query(
        """
        SELECT *
        FROM ExpenseEntity
        WHERE (tag = :tag OR :tag IS NULL) AND strftime('%m-%Y', dateMillis / 1000, 'unixepoch') = :date
    """
    )
    fun getExpensesByTagForDate(
        tag: String?,
        date: String
    ): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TagEntity)

    @Query("UPDATE ExpenseEntity SET tag = :tag WHERE id IN (:ids)")
    suspend fun setTagToExpenses(tag: String?, ids: List<Long>)

    @Query("UPDATE ExpenseEntity SET tag = NULL WHERE tag = :name")
    suspend fun removeTagFromExpenses(name: String)

    @Query("DELETE FROM TagEntity WHERE tagName = :name")
    suspend fun deleteTag(name: String)

    @Transaction
    suspend fun removeAndDeleteTag(name: String) {
        removeTagFromExpenses(name)
        deleteTag(name)
    }
}