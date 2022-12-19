package dev.ridill.stonkswallet.feature_settings.presentation.settings

import dev.ridill.stonkswallet.core.domain.model.AppTheme

interface SettingsActions {
    fun onThemePreferenceClick()
    fun onAppThemeSelectionDismiss()
    fun onAppThemeSelectionConfirm(theme: AppTheme)

    fun onExpenditureLimitPreferenceClick()
    fun onExpenditureLimitUpdateDismiss()
    fun onExpenditureLimitUpdateConfirm(amount: String)
    fun onShowLowBalanceUnderPercentPreferenceClick()
    fun onShowLowBalanceUnderPercentUpdateDismiss()
    fun onShowLowBalanceUnderPercentUpdateConfirm(value: Float)

    fun onAutoAddExpenseClick()
    fun onAutoAddExpenseDismiss()
    fun onAutoAddExpenseConfirm()

    fun onGoogleAccountSelectionClick()
    fun onPerformBackupClick()
    fun onCancelOngoingBackupClick()
}