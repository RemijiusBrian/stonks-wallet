package dev.ridill.stonkswallet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import dev.ridill.stonkswallet.feature_dashboard.data.repository.DashboardRepositoryImpl
import dev.ridill.stonkswallet.feature_dashboard.domain.repository.DashboardRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    fun provideDashboardRepository(
        preferencesManager: PreferencesManager,
        expenseRepository: ExpenseRepository
    ): DashboardRepository = DashboardRepositoryImpl(preferencesManager, expenseRepository)
}