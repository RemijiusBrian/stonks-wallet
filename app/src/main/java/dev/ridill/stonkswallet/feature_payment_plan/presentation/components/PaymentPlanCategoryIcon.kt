package dev.ridill.stonkswallet.feature_payment_plan.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory

@Composable
fun PaymentPlanCategoryIcon(
    category: PaymentCategory,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    size: Dp = DefaultSize,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = contentColorFor(containerColor)
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        modifier = Modifier
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        Box(
            modifier = modifier
                .padding(SpacingMedium)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(category.icon),
                contentDescription = category.label,
                modifier = Modifier
                    .size(size),
                tint = contentColor
            )
        }
    }
}

private val DefaultSize = 24.dp