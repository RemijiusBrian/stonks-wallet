package dev.ridill.stonkswallet.feature_settings.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import dev.ridill.stonkswallet.core.domain.model.AppTheme
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.feature_settings.domain.accounts.AccountsService
import dev.ridill.stonkswallet.feature_settings.domain.backup.BackupFileService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    private val accountsService: AccountsService,
    private val backupFileService: BackupFileService
) : ViewModel(), SettingsActions {

    private val preferences = preferencesManager.preferences

    private val showThemeSelection = savedStateHandle.getLiveData("showThemeSelection", false)
    private val showExpenditureLimitUpdate =
        savedStateHandle.getLiveData("showExpenditureLimitUpdate", false)
    private val showBalanceWarningPercentPicker =
        savedStateHandle.getLiveData("showBalanceWarningPercentPicker", false)

    private val showAutoAddExpenseDescription =
        savedStateHandle.getStateFlow(KEY_SHOW_AUTO_ADD_EXPENSE_DESC, false)

    private val eventsChannel = Channel<SettingsEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    val state = combineTuple(
        preferences,
        showThemeSelection.asFlow(),
        showExpenditureLimitUpdate.asFlow(),
        showBalanceWarningPercentPicker.asFlow(),
        showAutoAddExpenseDescription
    ).map { (
                preferences,
                showThemeSelection,
                showExpenditureUpdate,
                showBalanceWarningPercentPicker,
                showAutoAddExpenseDescription
            ) ->
        SettingsState(
            appTheme = preferences.theme,
            expenditureLimit = TextUtil.formatAmountWithCurrency(preferences.expenditureLimit),
            showThemeSelection = showThemeSelection,
            showExpenditureUpdate = showExpenditureUpdate,
            showBalanceWarningPercentPicker = showBalanceWarningPercentPicker,
            balanceWarningPercent = preferences.balanceWarningPercent,
            showAutoAddExpenseDescription = showAutoAddExpenseDescription,
            backupAccount = preferences.backupAccount
        )
    }.asLiveData()

    override fun onThemePreferenceClick() {
        showThemeSelection.value = true
    }

    override fun onAppThemeSelectionDismiss() {
        showThemeSelection.value = false
    }

    override fun onAppThemeSelectionConfirm(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.updateAppTheme(theme)
            showThemeSelection.value = false
        }
    }

    override fun onExpenditureLimitPreferenceClick() {
        showExpenditureLimitUpdate.value = true
    }

    override fun onExpenditureLimitUpdateDismiss() {
        showExpenditureLimitUpdate.value = false
    }

    override fun onExpenditureLimitUpdateConfirm(amount: String) {
        val parsedAmount = amount.toLongOrNull() ?: return
        viewModelScope.launch {
            if (parsedAmount < 0L) {
                eventsChannel.send(
                    SettingsEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_amount),
                        true
                    )
                )
                return@launch
            }
            preferencesManager.updateExpenditureLimit(parsedAmount)
            eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.expenditure_limit_updated)))
            showExpenditureLimitUpdate.value = false
        }
    }

    override fun onShowLowBalanceUnderPercentPreferenceClick() {
        showBalanceWarningPercentPicker.value = true
    }

    override fun onShowLowBalanceUnderPercentUpdateDismiss() {
        showBalanceWarningPercentPicker.value = false
    }

    override fun onShowLowBalanceUnderPercentUpdateConfirm(value: Float) {
        viewModelScope.launch {
            preferencesManager.updateBalanceWarningPercent(value)
            showBalanceWarningPercentPicker.value = false
            eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.value_updated)))
        }
    }

    override fun onAutoAddExpenseClick() {
        savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = true
    }

    override fun onAutoAddExpenseDismiss() {
        savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = false
    }

    override fun onAutoAddExpenseConfirm() {
        viewModelScope.launch {
            savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = false
            eventsChannel.send(SettingsEvent.RequestSmsPermission)
        }
    }

    fun onGoogleAccountSelected(data: Bundle) = viewModelScope.launch {
        val account = accountsService.parseAccountName(data)
        preferencesManager.updateBackupAccount(account.orEmpty())
    }

    override fun onGoogleAccountSelectionClick() {
        viewModelScope.launch {
            val intent = accountsService.buildAccountSelectionIntent()
            eventsChannel.send(SettingsEvent.LaunchGoogleAccountSelection(intent))
        }
    }

    override fun onPerformBackupClick() {
        viewModelScope.launch {
            eventsChannel.send(SettingsEvent.LaunchBackupExportPathSelector)
        }
    }

    fun onExportPathSelected(exportPath: Uri) = viewModelScope.launch {
        backupFileService.exportFile(exportPath)
    }

    override fun onCancelOngoingBackupClick() {
        // TODO: Cancel ongoing backup
    }

    sealed class SettingsEvent {
        data class ShowUiMessage(val message: UiText, val error: Boolean = false) : SettingsEvent()
        data class LaunchGoogleAccountSelection(val intent: Intent) : SettingsEvent()
        object RequestSmsPermission : SettingsEvent()
        object LaunchBackupExportPathSelector : SettingsEvent()
    }
}

private const val KEY_SHOW_AUTO_ADD_EXPENSE_DESC = "KEY_SHOW_AUTO_ADD_EXPENSE_DESC"