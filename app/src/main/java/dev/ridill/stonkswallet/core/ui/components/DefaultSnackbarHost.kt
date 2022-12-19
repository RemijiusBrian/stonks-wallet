package dev.ridill.stonkswallet.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.core.ui.theme.SpacingSmall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun DefaultSnackbarHost(
    snackbarController: SnackbarController,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarController.snackbarHostState,
        modifier = modifier,
        snackbar = { snackbarData ->
            DefaultSnackbar(
                visuals = snackbarData.visuals as XTSnackbarVisuals,
                performAction = snackbarData::performAction,
                onDismiss = snackbarData::dismiss
            )
        }
    )
}

@Composable
private fun DefaultSnackbar(
    visuals: XTSnackbarVisuals,
    performAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (visuals) {
        is XTSnackbarVisuals.Error -> MaterialTheme.colorScheme.errorContainer
        is XTSnackbarVisuals.Message -> MaterialTheme.colorScheme.inverseSurface
    }
    Snackbar(
        action = visuals.actionLabel?.let { label ->
            {
                TextButton(
                    onClick = performAction,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = contentColorFor(containerColor)
                    )
                ) { Text(label) }
            }
        },
        dismissAction = if (visuals.withDismissAction) {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_dismiss))
                }
            }
        } else null,
        shape = MaterialTheme.shapes.medium,
        containerColor = containerColor,
        contentColor = contentColorFor(containerColor),
        dismissActionContentColor = contentColorFor(containerColor),
        modifier = modifier.padding(SpacingMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (visuals is XTSnackbarVisuals.Error) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = stringResource(R.string.content_description_error),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(SpacingSmall))
            }
            Text(visuals.message)
        }
    }
}

class SnackbarController(
    val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) {
    private var snackbarJob: Job? = null

    init {
        dismissCurrentJob()
    }

    fun showSnackbar(
        message: String,
        error: Boolean = false,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onActionPerformed: ((SnackbarResult) -> Unit)? = null
    ) {
        dismissCurrentJob()
        snackbarJob = coroutineScope.launch {
            val snackbarVisuals = if (error) XTSnackbarVisuals.Error(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                withDismissAction = withDismissAction
            ) else XTSnackbarVisuals.Message(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                withDismissAction = withDismissAction
            )
            val result = snackbarHostState.showSnackbar(snackbarVisuals)
            if (actionLabel != null && onActionPerformed != null) {
                onActionPerformed.invoke(result)
            }
        }
    }

    private fun dismissCurrentJob() {
        snackbarJob?.cancel()
    }
}

@Composable
fun rememberSnackbarController(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SnackbarController = remember(snackbarHostState, coroutineScope) {
    SnackbarController(
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )
}

sealed class XTSnackbarVisuals : SnackbarVisuals {
    data class Message(
        override val message: String,
        override val actionLabel: String? = null,
        override val duration: SnackbarDuration = SnackbarDuration.Short,
        override val withDismissAction: Boolean = false
    ) : XTSnackbarVisuals()

    data class Error(
        override val message: String,
        override val actionLabel: String? = null,
        override val duration: SnackbarDuration = SnackbarDuration.Short,
        override val withDismissAction: Boolean = false
    ) : XTSnackbarVisuals()
}