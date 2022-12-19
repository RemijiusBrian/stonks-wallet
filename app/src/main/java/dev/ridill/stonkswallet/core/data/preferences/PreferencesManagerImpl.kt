package dev.ridill.stonkswallet.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import dev.ridill.stonkswallet.core.domain.model.AppTheme
import dev.ridill.stonkswallet.core.domain.model.SWPreferences
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class PreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : PreferencesManager {

    override val preferences = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            val expenditureLimit = preferences[Keys.EXPENDITURE_LIMIT] ?: 0L
            val theme = AppTheme.valueOf(
                preferences[Keys.APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
            )
            val balanceWarningPercent = preferences[Keys.BALANCE_WARNING_PERCENT] ?: 0.25f
            val backupAccount = preferences[Keys.BACKUP_ACCOUNT_ID]

            SWPreferences(
                expenditureLimit = expenditureLimit,
                theme = theme,
                balanceWarningPercent = balanceWarningPercent,
                backupAccount = backupAccount
            )
        }

    override suspend fun updateExpenditureLimit(limit: Long) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[Keys.EXPENDITURE_LIMIT] = limit
            }
        }
    }

    override suspend fun updateAppTheme(theme: AppTheme) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[Keys.APP_THEME] = theme.name
            }
        }
    }

    override suspend fun updateBalanceWarningPercent(value: Float) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[Keys.BALANCE_WARNING_PERCENT] = value
            }
        }
    }

    override suspend fun updateBackupAccount(mailId: String) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[Keys.BACKUP_ACCOUNT_ID] = mailId
            }
        }
    }

    private object Keys {
        val EXPENDITURE_LIMIT = longPreferencesKey("EXPENDITURE_LIMIT")
        val APP_THEME = stringPreferencesKey("APP_THEME")
        val BALANCE_WARNING_PERCENT = floatPreferencesKey("BALANCE_WARNING_PERCENT")
        val BACKUP_ACCOUNT_ID = stringPreferencesKey("BACKUP_ACCOUNT_ID")
    }
}