package dev.ridill.stonkswallet.feature_expenses.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.theme.ContentAlpha
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.core.ui.theme.SpacingSmall
import dev.ridill.stonkswallet.core.ui.theme.SpacingXSmall
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.core.util.onVariant

@Composable
fun TagInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(TagColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_tags),
                contentDescription = stringResource(R.string.content_description_create_new_tag)
            )
        },
        title = { Text(stringResource(R.string.create_tag)) },
        text = {
            TagInputLayout(
                name = input,
                onNameChange = { input = it },
                selectedColor = selectedColor,
                onColorSelect = { selectedColor = it },
                onClear = { input = "" },
                onSubmit = { onConfirm(input, selectedColor) },
                modifier = modifier
            )
        },
        confirmButton = {
            FilledTonalButton(onClick = { onConfirm(input, selectedColor) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun TagInputLayout(
    name: String,
    onNameChange: (String) -> Unit,
    selectedColor: Color,
    onColorSelect: (Color) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputValid by remember(name) {
        derivedStateOf { name.isNotEmpty() }
    }

    Column(
        modifier = modifier
            .padding(SpacingSmall)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                if (it.length < Constants.TAG_NAME_MAX_LENGTH)
                    onNameChange(it)
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = inputValid,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.content_description_clear)
                        )
                    }
                }
            },
            label = { Text(stringResource(R.string.tag_input_label)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (inputValid) onSubmit.invoke() }
            ),
            placeholder = {
                Text(stringResource(R.string.max_chars, Constants.TAG_NAME_MAX_LENGTH))
            },
            maxLines = 2,
            shape = MaterialTheme.shapes.medium
        )
        Spacer(Modifier.height(SpacingSmall))
        Text(
            text = stringResource(R.string.color),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.PERCENT_60)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingSmall),
            contentPadding = PaddingValues(
                vertical = SpacingXSmall,
                horizontal = SpacingMedium
            )
        ) {
            items(TagColors, key = { it.toArgb() }) { color ->
                ColorSelector(
                    color = color,
                    selected = selectedColor == color,
                    onClick = { onColorSelect(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorSelector(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = color.onVariant()
) {
    val borderWidthFraction by animateFloatAsState(targetValue = if (selected) 0.30f else 0.10f)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(ColorSelectorSize)
            .clickable { onClick() }
            .drawBehind {
                drawCircle(color)
                drawCircle(
                    color = borderColor,
                    style = Stroke(ColorSelectorSize.toPx() * borderWidthFraction)
                )
            }
    )
}

private val ColorSelectorSize = 32.dp
private val TagColors = listOf<Color>(
    Color(0xFF91BBF2),
    Color(0xFF023059),
    Color(0xFF011526),
    Color(0xFFC0C1C2),
    Color(0xFF747575),
    Color(0xFF353536),
    Color(0xFF7A1B1A),
    Color(0xFF611614),
    Color(0xFF3D0E0D)
)