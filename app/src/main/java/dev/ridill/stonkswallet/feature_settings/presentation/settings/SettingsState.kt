package dev.ridill.stonkswallet.feature_settings.presentation.settings

import dev.ridill.stonkswallet.core.domain.model.AppTheme

data class SettingsState(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val expenditureLimit: String = "",
    val showThemeSelection: Boolean = false,
    val showExpenditureUpdate: Boolean = false,
    val balanceWarningPercent: Float = 0f,
    val showBalanceWarningPercentPicker: Boolean = false,
    val showAutoAddExpenseDescription: Boolean = false,
    val backupAccount: String? = null
) {
    companion object {
        val INITIAL = SettingsState()
    }
}