package dev.ridill.stonkswallet.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.core.ui.theme.SpacingSmall

@Composable
fun ListLabel(
    @StringRes label: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(label),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .paddingFromBaseline(
                top = SpacingMedium,
                bottom = SpacingSmall
            )
    )
}