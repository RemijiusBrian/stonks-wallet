package dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan

import android.Manifest
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.core.ui.theme.SpacingSmall
import dev.ridill.stonkswallet.core.ui.theme.WEIGHT_1
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.isPermanentlyDenied
import dev.ridill.stonkswallet.core.util.timeMillis
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory
import dev.ridill.stonkswallet.feature_payment_plan.presentation.components.PaymentPlanCategoryIcon

@Composable
fun AddEditPaymentPlanScreenContent(
    name: String,
    amount: String,
    repeatPeriod: Int?,
    state: AddEditPaymentPlanScreenState,
    snackbarController: SnackbarController,
    actions: AddEditPaymentPlanActions,
    navigateUp: () -> Unit,
    isEditMode: Boolean,
    navigateToPermissionSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TransparentTopAppBar(
                title = if (isEditMode) R.string.edit_payment_plan
                else R.string.add_payment_plan,
                navigationIcon = { BackArrowButton(onClick = navigateUp) },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = actions::onDeleteClick) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = stringResource(R.string.content_description_delete_expense)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = actions::onSave) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = stringResource(R.string.content_description_save)
                )
            }
        },
        snackbarHost = { DefaultSnackbarHost(snackbarController) }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        else null
        var showNotificationPermissionRationale by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(SpacingMedium)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(SpacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Bill Category
                BillCategorySelection(
                    category = state.category,
                    onCategoryClick = actions::onCategoryClick
                )
                IconButton(
                    onClick = { showNotificationPermissionRationale = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (notificationPermissionState?.status?.isGranted == false) Icons.Outlined.NotificationsOff
                        else Icons.Outlined.NotificationsActive,
                        contentDescription = stringResource(R.string.content_description_notification_status)
                    )
                }
            }
            Spacer(Modifier.height(SpacingSmall))

            // Description Input
            LabelAndInput(
                label = R.string.description,
                value = name,
                onValueChange = actions::onNameChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                placeholder = R.string.payment_plan_description_eg,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Amount input
            LabelAndInput(
                label = R.string.amount,
                value = amount,
                onValueChange = actions::onAmountChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                placeholder = R.string.amount_placeholder,
                leadingIcon = { Text(text = TextUtil.currencySymbol) }
            )

            // Due Date options
            DueOptions(
                dueDate = state.dueDate,
                onDatePickerClick = actions::onDatePickerClick,
                repeatPeriod = repeatPeriod,
                onRepeatPeriodChange = actions::onRepeatMonthsPeriodChange
            )
        }

        if (state.showDatePicker) {
            AlertDialog(
                onDismissRequest = actions::onDatePickerDismiss,
                confirmButton = {
                    TextButton(onClick = {}) {
                        Text(stringResource(R.string.action_confirm))
                    }
                },
                text = {
                    Calendar(
                        dateString = state.dueDate,
                        onDateSelect = { actions.onDueDateChange(it.timeMillis) },
                        update = {
                            /*minDate = currentLocalDate
                                .withDayOfMonth(1)
                                .timeMillis

                            if (state.isBillRecurring) {
                                maxDate = currentLocalDate
                                    .withDayOfMonth(currentLocalDate.month.maxLength())
                                    .timeMillis
                            }*/
                        }
                    )
                }
            )
        }

        if (state.showCategorySelection) {
            BillCategorySelection(
                currentCategory = state.category,
                onDismiss = actions::onCategorySelectionDismiss,
                onConfirm = actions::onCategorySelect
            )
        }

        if (state.showDeletionConfirmation) {
            SimpleConfirmationDialog(
                title = R.string.dialog_delete_payment_plan_title,
                text = R.string.dialog_delete_payment_plan_message,
                onDismiss = actions::onDeleteDismiss,
                onConfirm = actions::onDeleteConfirm,
                icon = Icons.Rounded.DeleteForever
            )
        }

        if (showNotificationPermissionRationale) {
            PermissionRationaleDialog(
                rationalMessage = R.string.permission_post_notifications_rationale,
                icon = ImageVector.vectorResource(R.drawable.ic_notification),
                onDismiss = { showNotificationPermissionRationale = false },
                onConfirm = {
                    showNotificationPermissionRationale = false
                    if (notificationPermissionState?.status?.isGranted == true) return@PermissionRationaleDialog
                    if (notificationPermissionState?.status?.isPermanentlyDenied() == true) {
                        navigateToPermissionSettings()
                    } else {
                        notificationPermissionState?.launchPermissionRequest()
                    }
                },
                permissionGranted = notificationPermissionState?.status?.isGranted == true
            )
        }
    }
}

@Composable
private fun BillCategorySelection(
    category: PaymentCategory,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PaymentPlanCategoryIcon(
            category = category,
            onClick = onCategoryClick
        )
        Spacer(Modifier.width(SpacingMedium))
        Text(
            text = category.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.click_to_select_category),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LabelAndInput(
    @StringRes label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(SpacingSmall))
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = MaterialTheme.shapes.small,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            placeholder = {
                placeholder?.let { Text(stringResource(it)) }
            },
            leadingIcon = leadingIcon
        )
    }
}

@Composable
private fun BillCategorySelection(
    currentCategory: PaymentCategory,
    onDismiss: () -> Unit,
    onConfirm: (PaymentCategory) -> Unit
) {
    var currentSelection by remember { mutableStateOf(currentCategory) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(onClick = { onConfirm(currentSelection) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        title = { Text(stringResource(R.string.select_category)) },
        text = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PaymentCategory.values().forEach { category ->
                    val selected = category == currentSelection
                    val tint by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary
                        else LocalContentColor.current
                    )
                    Surface(
                        selected = selected,
                        onClick = { currentSelection = category },
                        contentColor = tint,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(SpacingSmall)
                        ) {
                            Icon(
                                painter = painterResource(category.icon),
                                contentDescription = category.label
                            )
                            AnimatedVisibility(visible = selected) {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier
                                        .padding(horizontal = SpacingSmall)
                                )
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Category,
                contentDescription = stringResource(R.string.content_description_payment_plan_category)
            )
        }
    )
}

@Composable
private fun DueOptions(
    dueDate: String,
    onDatePickerClick: () -> Unit,
    repeatPeriod: Int?,
    onRepeatPeriodChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        DueDateSelection(
            dueDate = dueDate,
            onDatePickerClick = onDatePickerClick,
            modifier = Modifier
                .weight(WEIGHT_1)
        )
        Spacer(Modifier.width(SpacingMedium))
        ReminderOptions(
            repeatPeriod = repeatPeriod,
            onRepeatPeriodChange = onRepeatPeriodChange,
            modifier = Modifier
                .weight(WEIGHT_1)
        )
    }
}

@Composable
private fun ReminderOptions(
    repeatPeriod: Int?,
    onRepeatPeriodChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    BasicTextField(
        value = repeatPeriod.toString(),
        onValueChange = onRepeatPeriodChange,
        textStyle = textStyle,
        singleLine = true,
        maxLines = 1,
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp),
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalTextStyle provides textStyle) {
                    Text(
                        pluralStringResource(
                            id = R.plurals.repeat_period,
                            count = repeatPeriod ?: 0,
                            repeatPeriod ?: 0
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun DueDateSelection(
    dueDate: String,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDatePickerClick) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = stringResource(R.string.content_description_date_picker)
            )
        }
        Text(
            text = stringResource(R.string.due_on_date, dueDate),
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis
        )
    }
}