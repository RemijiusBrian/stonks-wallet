package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.accompanist.permissions.rememberPermissionState
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.rememberSnackbarController
import dev.ridill.stonkswallet.core.util.exhaustive
import dev.ridill.stonkswallet.core.util.isPermanentlyDenied
import dev.ridill.stonkswallet.core.util.log
import dev.ridill.stonkswallet.feature_settings.presentation.settings.SettingsScreenContent
import dev.ridill.stonkswallet.feature_settings.presentation.settings.SettingsState
import dev.ridill.stonkswallet.feature_settings.presentation.settings.SettingsViewModel

object SettingsScreenSpec : BottomBarScreenSpec {

    override val icon: ImageVector = Icons.Outlined.Settings

    override val label: Int = R.string.destination_settings

    override val navHostRoute: String = "settings"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: SettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.observeAsState(SettingsState.INITIAL)

        val context = LocalContext.current
        val snackbarController = rememberSnackbarController()
        val googleAccountSelectionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.extras?.let(viewModel::onGoogleAccountSelected)
                }
            }
        )
        val smsPermissionState = rememberPermissionState(Manifest.permission.RECEIVE_SMS)
        val backupLocationSelectionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
            onResult = { uri ->
                log { uri?.isAbsolute }
                uri?.let(viewModel::onExportPathSelected)
            }
        )

        LaunchedEffect(context) {
            viewModel.events.collect { event ->
                when (event) {
                    is SettingsViewModel.SettingsEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            event.message.asString(context),
                            event.error
                        )
                    }
                    is SettingsViewModel.SettingsEvent.LaunchGoogleAccountSelection -> {
                        googleAccountSelectionLauncher.launch(event.intent)
                    }
                    SettingsViewModel.SettingsEvent.RequestSmsPermission -> {
                        if (smsPermissionState.status.isPermanentlyDenied()) {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        } else {
                            smsPermissionState.launchPermissionRequest()
                        }
                    }
                    SettingsViewModel.SettingsEvent.LaunchBackupExportPathSelector -> {
                        backupLocationSelectionLauncher.launch(null)
                    }
                }.exhaustive
            }
        }

        SettingsScreenContent(
            snackbarController = snackbarController,
            context = context,
            state = state,
            actions = viewModel,
            navigateUp = navController::popBackStack,
            navigateToNotificationSettings = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
    }
}