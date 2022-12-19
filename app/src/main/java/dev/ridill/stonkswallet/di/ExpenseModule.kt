package dev.ridill.stonkswallet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.stonkswallet.core.data.local.db.SWDatabase
import dev.ridill.stonkswallet.core.notification.NotificationHelper
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_expenses.data.local.ExpenseDao
import dev.ridill.stonkswallet.feature_expenses.data.local.TagsDao
import dev.ridill.stonkswallet.feature_expenses.data.repository.AddEditExpenseRepositoryImpl
import dev.ridill.stonkswallet.feature_expenses.data.repository.DetailedViewRepositoryImpl
import dev.ridill.stonkswallet.feature_expenses.data.repository.ExpenseRepositoryImpl
import dev.ridill.stonkswallet.feature_expenses.data.repository.TagsRepositoryImpl
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.notification.ExpenseNotificationHelper
import dev.ridill.stonkswallet.feature_expenses.domain.repository.AddEditExpenseRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.DetailedViewRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.TagsRepository
import dev.ridill.stonkswallet.feature_payment_plan.data.local.PaymentPlanDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpenseModule {

    @Singleton
    @Provides
    fun provideExpenseDao(database: SWDatabase): ExpenseDao = database.expenseDao

    @Singleton
    @Provides
    fun provideTagsDao(database: SWDatabase): TagsDao = database.tagsDao

    @Singleton
    @Provides
    fun provideExpenseRepository(
        dao: ExpenseDao,
        dispatcherProvider: DispatcherProvider,
        paymentPlanDao: PaymentPlanDao
    ): ExpenseRepository = ExpenseRepositoryImpl(dao, dispatcherProvider, paymentPlanDao)

    @Provides
    fun provideTagsRepository(
        dao: TagsDao,
        dispatcherProvider: DispatcherProvider
    ): TagsRepository = TagsRepositoryImpl(dao, dispatcherProvider)

    @Provides
    fun provideAddEditExpenseRepository(
        expenseRepository: ExpenseRepository,
        tagsRepository: TagsRepository
    ): AddEditExpenseRepository = AddEditExpenseRepositoryImpl(expenseRepository, tagsRepository)

    @Provides
    fun provideDetailedViewRepository(
        expenseRepository: ExpenseRepository,
        tagsRepository: TagsRepository
    ): DetailedViewRepository = DetailedViewRepositoryImpl(expenseRepository, tagsRepository)

    @Provides
    fun provideExpenseNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper<Expense> = ExpenseNotificationHelper(context)
}