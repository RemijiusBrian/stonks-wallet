package dev.ridill.stonkswallet.core.domain.model

import dev.ridill.stonkswallet.core.util.Constants

data class SWPreferences(
    val expenditureLimit: Long,
    val theme: AppTheme,
    val balanceWarningPercent: Float,
    val backupAccount: String?
) {
    val balanceWarningEnabled: Boolean
        get() = balanceWarningPercent > Constants.ZERO_F

    val isExpenditureLimitSet: Boolean
        get() = expenditureLimit > 0
}