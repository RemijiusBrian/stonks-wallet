package dev.ridill.stonkswallet.feature_settings.domain.accounts

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.first

class AccountsService(
    private val accountManager: AccountManager,
    private val preferencesManager: PreferencesManager,
    private val context: Context
) {

    companion object {
        private const val ACCOUNT_TYPE_GOOGLE = "com.google"
    }

    fun parseAccountName(data: Bundle): String? =
        data.getString(AccountManager.KEY_ACCOUNT_NAME)

    fun buildAccountSelectionIntent(): Intent = AccountManager.newChooseAccountIntent(
        null,
        null,
        arrayOf(ACCOUNT_TYPE_GOOGLE),
        "",
        null,
        null,
        null
    )

    suspend fun getBackupAccount(): Account? {
        val backupAccount = preferencesManager.preferences.first().backupAccount
        return accountManager.getAccountsByType(ACCOUNT_TYPE_GOOGLE)
            .find { it.name == backupAccount }
    }
}