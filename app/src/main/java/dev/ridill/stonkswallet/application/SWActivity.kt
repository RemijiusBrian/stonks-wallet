package dev.ridill.stonkswallet.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.stonkswallet.core.domain.model.AppTheme
import dev.ridill.stonkswallet.core.ui.navigation.SWNavHost
import dev.ridill.stonkswallet.core.ui.theme.StonksWalletTheme

@AndroidEntryPoint
class SWActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val viewModel: SWViewModel = hiltViewModel()
            val preferences by viewModel.preferences.observeAsState()

            @Suppress("NAME_SHADOWING")
            preferences?.let { preferences ->
                val darkTheme = when (preferences.theme) {
                    AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                }
                ScreenContent(darkTheme)
            }
        }
    }
}

@Composable
fun ScreenContent(darkTheme: Boolean) {
    val navController = rememberNavController()
    StonksWalletTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { SWNavHost(navController) }
    }
}