package dev.ridill.stonkswallet.feature_expenses.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toExpense
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toTagOverview
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.TagOverview
import dev.ridill.stonkswallet.feature_expenses.domain.repository.DetailedViewRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DetailedViewRepositoryImpl(
    private val expenseRepo: ExpenseRepository,
    private val tagsRepo: TagsRepository
) : DetailedViewRepository {

    override fun getYearsList(): Flow<List<String>> = expenseRepo.getYearsOfExpenses()
        .map { years ->
            // Pad list with future years if size smaller than 10 elements
            years.toMutableList().apply {
                if (size >= 10) return@apply
                var lastElement = this.lastOrNull() ?: (DateUtil.currentDate().year - 1)
                repeat(10 - size) {
                    lastElement++
                    add(lastElement)
                }
            }.toList()
        }.map { years ->
            years.map { it.toString() }
        }

    override fun getTotalExpenditure(monthAndYear: String): Flow<Double> =
        expenseRepo.getExpenditureForDate(monthAndYear)

    override fun getTagOverviews(
        totalExpenditure: Double,
        monthAndYear: String
    ): Flow<List<TagOverview>> = tagsRepo.getTagsWithExpenditures(monthAndYear).map { relations ->
        relations.map { it.toTagOverview(totalExpenditure) }
    }

    override fun getExpenses(tag: String?, monthAndYear: String): Flow<List<Expense>> =
        tagsRepo.getExpensesByTagForDate(tag, monthAndYear).map { entities ->
            entities.map { it.toExpense() }
        }

    override suspend fun tagExpenses(tag: String?, expenseIds: List<Long>) =
        tagsRepo.tagExpenses(tag, expenseIds)

    override suspend fun deleteTag(tag: String) =
        tagsRepo.delete(tag)

    override suspend fun createTag(tag: String, color: Color) =
        tagsRepo.cacheTag(TagEntity(tag, color.toArgb()))

    override suspend fun deleteExpenses(ids: List<Long>) =
        expenseRepo.deleteMultipleExpenses(ids)
}