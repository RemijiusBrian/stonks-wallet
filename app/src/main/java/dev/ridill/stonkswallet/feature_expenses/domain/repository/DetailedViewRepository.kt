package dev.ridill.stonkswallet.feature_expenses.domain.repository

import androidx.compose.ui.graphics.Color
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.TagOverview
import kotlinx.coroutines.flow.Flow

interface DetailedViewRepository {

    fun getYearsList(): Flow<List<String>>

    fun getTotalExpenditure(monthAndYear: String): Flow<Double>

    fun getTagOverviews(totalExpenditure: Double, monthAndYear: String): Flow<List<TagOverview>>

    fun getExpenses(tag: String?, monthAndYear: String): Flow<List<Expense>>

    suspend fun tagExpenses(tag: String?, expenseIds: List<Long>)

    suspend fun deleteTag(tag: String)

    suspend fun createTag(tag: String, color: Color)

    suspend fun deleteExpenses(ids: List<Long>)
}