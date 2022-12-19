package dev.ridill.stonkswallet.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = MoneyGreen80,
    onPrimary = MoneyGreen20,
    primaryContainer = MoneyGreen30,
    onPrimaryContainer = MoneyGreen90,
    inversePrimary = MoneyGreen40,
    secondary = DarkGreen80,
    onSecondary = DarkGreen20,
    secondaryContainer = DarkGreen30,
    onSecondaryContainer = DarkGreen90,
    tertiary = Violet80,
    onTertiary = Violet20,
    tertiaryContainer = Violet30,
    onTertiaryContainer = Violet90,
    background = Grey20,
    onBackground = Grey90,
    surface = GreenGrey30,
    onSurface = GreenGrey80,
    surfaceVariant = GreenGrey30,
    onSurfaceVariant = GreenGrey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey10,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    outline = GreenGrey80
)

private val LightColorScheme = lightColorScheme(
    primary = MoneyGreen40,
    onPrimary = Color.White,
    primaryContainer = MoneyGreen90,
    onPrimaryContainer = MoneyGreen10,
    inversePrimary = MoneyGreen80,
    secondary = DarkGreen40,
    onSecondary = Color.White,
    secondaryContainer = DarkGreen90,
    onSecondaryContainer = DarkGreen10,
    tertiary = Violet40,
    onTertiary = Color.White,
    tertiaryContainer = Violet90,
    onTertiaryContainer = Violet10,
    background = Grey99,
    onBackground = Grey10,
    surface = GreenGrey90,
    onSurface = GreenGrey30,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    outline = GreenGrey50
)

@Composable
fun StonksWalletTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = if (dynamicColor) {
        if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
        else dynamicLightColorScheme(LocalContext.current)
    } else {
        if (darkTheme) DarkColorScheme
        else LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (darkTheme) colorScheme.surface
            else colorScheme.primary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}