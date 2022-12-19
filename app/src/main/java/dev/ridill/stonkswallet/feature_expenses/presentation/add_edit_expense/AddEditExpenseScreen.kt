package dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.theme.*
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.onVariant
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.presentation.components.TagInputDialog

@Composable
fun AddEditExpenseScreenContent(
    snackbarController: SnackbarController,
    isEditMode: Boolean,
    amountInput: String,
    noteInput: String,
    state: AddEditExpenseState,
    actions: AddEditExpenseActions
) {
    BackHandler(
        enabled = state.savable,
        onBack = actions::onTryNavigateUp
    )

    Scaffold(
        snackbarHost = { DefaultSnackbarHost(snackbarController) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            id = if (isEditMode) R.string.edit_expense
                            else R.string.add_expense
                        )
                    )
                },
                navigationIcon = { BackArrowButton(onClick = actions::onTryNavigateUp) },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = actions::onDeleteClick) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = stringResource(R.string.content_description_delete_expense)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            if (state.savable) {
                FloatingActionButton(onClick = actions::onSave) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = stringResource(R.string.content_description_save)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = ScrollEndPadding, horizontal = SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(SpacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmountInput(
                value = amountInput,
                onValueChange = actions::onAmountChange
            )
            TextField(
                value = noteInput,
                onValueChange = actions::onNoteChange,
                shape = MaterialTheme.shapes.medium,
                textStyle = TextStyle.Default.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .defaultMinSize(minWidth = InputFieldMinWidth)
                    .widthIn(max = InputFieldMaxWidth),
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                        .copy(alpha = ContentAlpha.PERCENT_32)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { actions.onSave() }
                ),
                placeholder = { Text(stringResource(R.string.add_note)) },
                maxLines = 2
            )
            Text(
                text = stringResource(R.string.label_tag),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                mainAxisSpacing = SpacingSmall
            ) {
                state.tagsList.forEach { tag ->
                    FilterChip(
                        selected = tag.name == state.selectedTag?.name,
                        onClick = { actions.onTagSelect(tag) },
                        label = { Text(tag.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tag.color,
                            selectedLabelColor = tag.color.onVariant()
                        )
                    )
                }
                ElevatedAssistChip(
                    onClick = actions::onNewTagClick,
                    label = { Text(stringResource(R.string.new_tag)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (state.showTagInput) Icons.Default.Close
                            else Icons.Default.Add,
                            contentDescription = stringResource(R.string.content_description_create_new_tag)
                        )
                    },
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        if (state.showTagInput) {
            TagInputDialog(
                onDismiss = actions::onNewTagDismiss,
                onConfirm = actions::onNewTagConfirm
            )
        }

        if (state.showDeleteConfirmation) {
            SimpleConfirmationDialog(
                title = R.string.dialog_delete_expense_title,
                text = R.string.dialog_delete_expense_message,
                onDismiss = actions::onDeleteDismiss,
                onConfirm = actions::onDeleteConfirm,
                icon = Icons.Rounded.DeleteForever
            )
        }

        if (state.showDiscardChangesMessage) {
            SimpleConfirmationDialog(
                title = R.string.dialog_discard_changes_title,
                text = R.string.dialog_discard_changes_message,
                onDismiss = actions::onDiscardChangedDismissed,
                onConfirm = actions::onDiscardChangedConfirmed,
                actionConfirm = R.string.action_discard
            )
        }
    }
}


@Composable
private fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displayMedium.copy(
        color = MaterialTheme.colorScheme.onBackground
    )
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.amount),
            style = MaterialTheme.typography.titleSmall
        )
        BasicTextField(
            value = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
            onValueChange = { onValueChange(it.text) },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                ) {
                    Text(
                        text = TextUtil.currencySymbol,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(SpacingXSmall))
                    Box {
                        this@Row.AnimatedVisibility(visible = value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.amount_placeholder),
                                style = textStyle,
                                color = MaterialTheme.colorScheme.onBackground
                                    .copy(alpha = ContentAlpha.PERCENT_16)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

private val InputFieldMinWidth = 80.dp
private val InputFieldMaxWidth = 240.dp

@Preview(showBackground = true)
@Composable
private fun PreviewScreenContent() {
    StonksWalletTheme {
        AddEditExpenseScreenContent(
            snackbarController = rememberSnackbarController(),
            isEditMode = true,
            amountInput = "100",
            noteInput = "",
            state = AddEditExpenseState.INITIAL,
            actions = object : AddEditExpenseActions {
                override fun onAmountChange(value: String) {}
                override fun onNoteChange(value: String) {}
                override fun onTagSelect(tag: Tag) {}
                override fun onNewTagClick() {}
                override fun onNewTagDismiss() {}
                override fun onNewTagConfirm(name: String, color: Color) {}
                override fun onDeleteClick() {}
                override fun onDeleteDismiss() {}
                override fun onDeleteConfirm() {}
                override fun onSave() {}
                override fun onTryNavigateUp() {}
                override fun onDiscardChangedDismissed() {}
                override fun onDiscardChangedConfirmed() {}
            }
        )
    }
}