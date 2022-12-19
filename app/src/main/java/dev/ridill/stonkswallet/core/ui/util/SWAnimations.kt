package dev.ridill.stonkswallet.core.ui.util

import androidx.compose.animation.*

fun <T> AnimatedContentScope<T>.verticalSpinner(
    slideUpFromBottom: () -> Boolean = { initialState isTransitioningTo targetState },
): ContentTransform = if (slideUpFromBottom()) {
    slideInVertically { height -> height } + fadeIn() with
            slideOutVertically { height -> -height } + fadeOut()
} else {
    slideInVertically { height -> -height } + fadeIn() with
            slideOutVertically { height -> height } + fadeOut()
} using SizeTransform(clip = false)