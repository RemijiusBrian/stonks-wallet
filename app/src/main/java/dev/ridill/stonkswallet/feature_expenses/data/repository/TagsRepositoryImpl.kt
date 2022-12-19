package dev.ridill.stonkswallet.feature_expenses.data.repository

import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_expenses.data.local.TagsDao
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.TagWithExpenditureRelation
import dev.ridill.stonkswallet.feature_expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TagsRepositoryImpl(
    private val dao: TagsDao,
    private val dispatcherProvider: DispatcherProvider
) : TagsRepository {

    override fun getAllTags(): Flow<List<TagEntity>> = dao.getTagsList()

    override fun getTagsWithExpenditures(date: String): Flow<List<TagWithExpenditureRelation>> =
        dao.getTagWithExpendituresForDate(date)

    override fun getExpensesByTagForDate(tag: String?, date: String): Flow<List<ExpenseEntity>> =
        dao.getExpensesByTagForDate(tag, date)

    override suspend fun cacheTag(tag: TagEntity) = withContext(dispatcherProvider.io) {
        dao.insert(tag)
    }

    override suspend fun tagExpenses(tag: String?, expenseIds: List<Long>) =
        withContext(dispatcherProvider.io) {
            dao.setTagToExpenses(tag, expenseIds)
        }

    override suspend fun delete(tag: String) {
        dao.removeAndDeleteTag(tag)
    }
}