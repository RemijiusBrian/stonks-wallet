package dev.ridill.stonkswallet.core.data.preferences

import dev.ridill.stonkswallet.core.domain.model.AppTheme
import dev.ridill.stonkswallet.core.domain.model.SWPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    companion object {
        const val NAME = "SW_PREFERENCES"
    }

    val preferences: Flow<SWPreferences>

    suspend fun updateExpenditureLimit(limit: Long)
    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateBalanceWarningPercent(value: Float)
    suspend fun updateBackupAccount(mailId: String)
}