package dev.ridill.stonkswallet.di

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.stonkswallet.core.data.local.db.SWDatabase
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManagerImpl
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.core.util.DispatcherProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()

    @Singleton
    @Provides
    fun provideDatabase(application: Application): SWDatabase =
        Room.databaseBuilder(
            application,
            SWDatabase::class.java,
            SWDatabase.NAME
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile(PreferencesManager.NAME) },
            migrations = listOf()
        )

    @Singleton
    @Provides
    fun providePreferencesManager(
        dataStore: DataStore<Preferences>,
        dispatcherProvider: DispatcherProvider
    ): PreferencesManager = PreferencesManagerImpl(dataStore, dispatcherProvider)

    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerCompat = NotificationManagerCompat.from(context)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope