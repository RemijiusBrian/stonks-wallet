package dev.ridill.stonkswallet.feature_expenses.domain.model

import androidx.compose.ui.graphics.Color

data class TagOverview(
    val tag: String,
    val color: Color,
    val amount: String,
    val percentOfLimit: Float
)