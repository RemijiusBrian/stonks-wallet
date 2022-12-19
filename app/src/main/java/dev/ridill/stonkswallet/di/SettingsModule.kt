package dev.ridill.stonkswallet.di

import android.accounts.AccountManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_settings.domain.accounts.AccountsService
import dev.ridill.stonkswallet.feature_settings.domain.backup.BackupFileService

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideAccountManager(@ApplicationContext context: Context): AccountManager =
        AccountManager.get(context)

    @Provides
    fun provideAccountsService(
        accountManager: AccountManager,
        preferencesManager: PreferencesManager,
        @ApplicationContext context: Context
    ): AccountsService = AccountsService(accountManager, preferencesManager, context)

    @Provides
    fun provideBackupFileService(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ): BackupFileService = BackupFileService(context, dispatcherProvider)
}