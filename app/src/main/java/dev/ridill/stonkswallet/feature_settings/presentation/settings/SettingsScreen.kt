package dev.ridill.stonkswallet.feature_settings.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.core.net.toUri
import dev.ridill.stonkswallet.BuildConfig
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.AppTheme
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.SettingsScreenSpec
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.feature_settings.presentation.components.BasicPreference
import dev.ridill.stonkswallet.feature_settings.presentation.components.ExpenditureLimitUpdateDialog
import dev.ridill.stonkswallet.feature_settings.presentation.components.SectionTitle
import kotlin.math.roundToInt

@Composable
fun SettingsScreenContent(
    snackbarController: SnackbarController,
    context: Context,
    state: SettingsState,
    actions: SettingsActions,
    navigateUp: () -> Unit,
    navigateToNotificationSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TransparentTopAppBar(
                title = SettingsScreenSpec.label,
                navigationIcon = { BackArrowButton(onClick = navigateUp) }
            )
        },
        snackbarHost = { DefaultSnackbarHost(snackbarController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // General Section
            SectionTitle(title = R.string.general)
            BasicPreference(
                title = R.string.theme,
                summary = stringResource(state.appTheme.label),
                icon = Icons.Default.BrightnessMedium,
                onClick = actions::onThemePreferenceClick
            )
            BasicPreference(
                title = R.string.notifications,
                icon = ImageVector.vectorResource(R.drawable.ic_notification),
                onClick = navigateToNotificationSettings
            )

            // Expense Section
            SectionTitle(title = R.string.expense)
            BasicPreference(
                title = R.string.expenditure_limit,
                summary = state.expenditureLimit,
                onClick = actions::onExpenditureLimitPreferenceClick
            )
            BasicPreference(
                title = R.string.show_warning_when_balance_under,
                summary = if (state.balanceWarningPercent > 0)
                    TextUtil.formatPercent(state.balanceWarningPercent)
                else stringResource(R.string.disabled),
                onClick = actions::onShowLowBalanceUnderPercentPreferenceClick
            )
            BasicPreference(
                title = R.string.auto_add_expenses,
                summary = stringResource(R.string.auto_add_expenses),
                onClick = actions::onAutoAddExpenseClick
            )

            // Backup Section
            SectionTitle(title = R.string.backup)
            BasicPreference(
                title = R.string.google_account,
                summary = state.backupAccount,
                icon = ImageVector.vectorResource(R.drawable.ic_google),
                onClick = actions::onGoogleAccountSelectionClick
            )
            BasicPreference(
                title = R.string.perform_data_backup,
                icon = Icons.Default.CloudUpload,
                onClick = actions::onPerformBackupClick
            )

            // Links Section
            SectionTitle(title = R.string.links)
            BasicPreference(
                title = R.string.contact_support,
                summary = stringResource(R.string.bug_report_feature_request),
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:ridilldev@gmail.com".toUri()
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            "${context.getString(R.string.app_name)} Feature Request/Bug Report"
                        )
                    }
                    context.startActivity(intent)
                }
            )

            // Info Section
            SectionTitle(title = R.string.info)
            BasicPreference(
                title = R.string.app_version,
                summary = BuildConfig.VERSION_NAME,
                icon = Icons.Default.Info
            )
        }

        if (state.showThemeSelection) {
            ThemeSelectionDialog(
                selectedTheme = state.appTheme,
                onDismiss = actions::onAppThemeSelectionDismiss,
                onConfirm = actions::onAppThemeSelectionConfirm
            )
        }

        if (state.showExpenditureUpdate) {
            ExpenditureLimitUpdateDialog(
                previousLimit = state.expenditureLimit,
                onDismiss = actions::onExpenditureLimitUpdateDismiss,
                onConfirm = actions::onExpenditureLimitUpdateConfirm
            )
        }

        if (state.showBalanceWarningPercentPicker) {
            SliderDialog(
                currentValue = state.balanceWarningPercent,
                onDismiss = actions::onShowLowBalanceUnderPercentUpdateDismiss,
                onConfirm = actions::onShowLowBalanceUnderPercentUpdateConfirm
            )
        }

        if (state.showAutoAddExpenseDescription) {
            PermissionRationaleDialog(
                rationalMessage = R.string.permission_receive_sms_rationale,
                icon = ImageVector.vectorResource(R.drawable.ic_message),
                onDismiss = actions::onAutoAddExpenseDismiss,
                onConfirm = actions::onAutoAddExpenseConfirm
            )
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    selectedTheme: AppTheme,
    onDismiss: () -> Unit,
    onConfirm: (AppTheme) -> Unit
) {
    var selection by remember { mutableStateOf(selectedTheme) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(selection) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(text = stringResource(R.string.select_theme)) },
        text = {
            Column {
                AppTheme.values().forEach { theme ->
                    RadioButtonWithLabel(
                        label = theme.label,
                        selected = theme == selection,
                        onClick = { selection = theme },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        },
        icon = {
            Icon(imageVector = Icons.Outlined.BrightnessMedium, contentDescription = null)
        }
    )
}

@Composable
private fun SliderDialog(
    currentValue: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selection by remember { mutableStateOf(currentValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.show_warning_below_percentage_selection_title)) },
        confirmButton = {
            TextButton(onClick = { onConfirm(selection) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        text = {
            Column {
                Text(
                    text = if (selection > Constants.ZERO_F)
                        stringResource(
                            R.string.warning_will_show_when_balance_drops_below,
                            (selection * 100).roundToInt()
                        )
                    else stringResource(R.string.disabled_low_warning_balance)
                )
                Slider(
                    value = selection,
                    onValueChange = { selection = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    steps = 100,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent
                    )
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.content_description_balance_low_warning)
            )
        },
        modifier = modifier
    )
}