package dev.ridill.stonkswallet.feature_expenses.domain.repository

import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.TagWithExpenditureRelation
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    fun getAllTags(): Flow<List<TagEntity>>

    fun getTagsWithExpenditures(date: String): Flow<List<TagWithExpenditureRelation>>

    fun getExpensesByTagForDate(
        tag: String?,
        date: String
    ): Flow<List<ExpenseEntity>>

    suspend fun cacheTag(tag: TagEntity)

    suspend fun tagExpenses(tag: String?, expenseIds: List<Long>)

    suspend fun delete(tag: String)
}