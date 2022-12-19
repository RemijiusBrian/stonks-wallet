package dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.DetailedViewScreenSpec
import dev.ridill.stonkswallet.core.ui.theme.*
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.core.util.onVariant
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.domain.model.TagOverview
import dev.ridill.stonkswallet.feature_expenses.presentation.components.BaseExpenseCardLayout
import dev.ridill.stonkswallet.feature_expenses.presentation.components.TagInputDialog
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@Composable
fun DetailedViewScreen(
    snackbarController: SnackbarController,
    state: DetailedViewState,
    actions: DetailedViewActions,
    navigateUp: () -> Unit
) {
    BackHandler(state.multiSelectionModeActive) {
        actions.onDismissMultiSelectionMode()
    }

    Scaffold(
        snackbarHost = { DefaultSnackbarHost(snackbarController) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.multiSelectionModeActive)
                            stringResource(
                                R.string.count_selected,
                                state.selectedExpenseIds.size
                            )
                        else stringResource(DetailedViewScreenSpec.label)
                    )
                },
                navigationIcon = {
                    if (state.multiSelectionModeActive) {
                        IconButton(onClick = actions::onDismissMultiSelectionMode) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.content_description_cancel_multi_selection_mode)
                            )
                        }
                    } else {
                        BackArrowButton(onClick = navigateUp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            TagOverviews(
                tagOverviews = state.tagOverviews,
                selectedTag = state.selectedTag,
                onTagClick = actions::onTagSelect,
                modifier = Modifier
                    .height(TagOverviewHeight),
                onTagDelete = actions::onTagDelete,
                onNewTagClick = actions::onNewTagClick
            )
            AnimatedVisibility(visible = state.multiSelectionModeActive) {
                Text(
                    text = stringResource(R.string.select_tag_to_assign_to_expenses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.PERCENT_60),
                    modifier = Modifier
                        .padding(horizontal = SpacingLarge, vertical = SpacingSmall)
                )
            }
            TotalExpenditure(state.totalExpenditure)
            AnimatedVisibility(!state.multiSelectionModeActive) {
                DateSelector(
                    yearsList = state.yearsList,
                    selectedYear = state.selectedYear,
                    onYearSelect = actions::onYearSelect,
                    selectedMonth = state.selectedMonth,
                    onMonthSelect = actions::onMonthSelect
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.expenses.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = SpacingMedium,
                            bottom = ScrollEndPadding,
                            start = SpacingSmall,
                            end = SpacingSmall
                        ),
                        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
                    ) {
                        if (state.multiSelectionModeActive) {
                            item(key = "MultiSelectionOptionsRow") {
                                MultiSelectionOptions(
                                    selectionState = state.expenseSelectionState,
                                    onSelectionStateChange = actions::onSelectionStateChange,
                                    onUntagClick = actions::onUntagExpensesClick,
                                    onDeleteClick = actions::onDeleteExpensesClick,
                                    modifier = Modifier
                                        .animateItemPlacement()
                                )
                            }
                        }
                        items(state.expenses, key = { it.id }) { expense ->
                            ExpenseListItem(
                                note = expense.note,
                                date = expense.dateFormatted,
                                amount = expense.amountFormatted,
                                selected = expense.id in state.selectedExpenseIds,
                                isClickable = state.multiSelectionModeActive,
                                onClick = { actions.onExpenseSelectionToggle(expense.id) },
                                onLongClick = { actions.onExpenseLongClick(expense.id) },
                                modifier = Modifier
                                    .animateItemPlacement()
                            )
                        }
                    }
                } else {
                    // TODO: Implement no data indicator
                }
            }
        }

        if (state.showTagInput) {
            TagInputDialog(
                onDismiss = actions::onNewTagDismiss,
                onConfirm = actions::onNewTagConfirm
            )
        }

        if (state.showTagDeletionConfirmation) {
            SimpleConfirmationDialog(
                title = R.string.dialog_delete_tag_title,
                text = R.string.dialog_delete_tag_message,
                onDismiss = actions::onTagDeleteDismiss,
                onConfirm = actions::onTagDeleteConfirm,
                icon = Icons.Outlined.DeleteForever
            )
        }

        if (state.showExpenseDeleteConfirmation) {
            SimpleConfirmationDialog(
                title = R.string.dialog_delete_selected_expense_title,
                text = R.string.dialog_delete_selected_expense_message,
                onDismiss = actions::onDeleteExpensesDismissed,
                onConfirm = actions::onDeleteExpensesConfirmed,
                icon = Icons.Rounded.DeleteForever
            )
        }
    }
}

@Composable
private fun TagOverviews(
    tagOverviews: List<TagOverview>,
    selectedTag: String?,
    onTagClick: (String) -> Unit,
    onTagDelete: (String) -> Unit,
    onNewTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            start = SpacingMedium,
            end = ScrollEndPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        items(tagOverviews, key = { it.tag }) { overview ->
            TagOverviewCard(
                name = overview.tag,
                color = overview.color,
                percentOfExpenditure = overview.percentOfLimit,
                amount = overview.amount,
                expanded = overview.tag == selectedTag,
                onClick = { onTagClick(overview.tag) },
                onDeleteClick = { onTagDelete(overview.tag) },
                isUntagged = overview.tag == Tag.Untagged.name,
                modifier = Modifier
                    .fillParentMaxHeight()
                    .animateItemPlacement()
            )
        }

        item(key = "NewTag") {
            Surface(
                tonalElevation = ElevationLevel1,
                shape = MaterialTheme.shapes.large,
                onClick = onNewTagClick
            ) {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .width(TagOverviewCardBaseWidth),
                    contentAlignment = Alignment.Center
                ) {
                    FilledTonalIconButton(
                        onClick = onNewTagClick,
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.content_description_create_new_tag)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagOverviewCard(
    name: String,
    color: Color,
    percentOfExpenditure: Float,
    amount: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isUntagged: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color = color.onVariant()
) {
    val transition = updateTransition(targetState = expanded, label = "tagSelection")
    val width by transition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "cardWidth",
        targetValueByState = { if (it) TagOverviewCardBaseWidth * 2f else TagOverviewCardBaseWidth }
    )
    val textSize by transition.animateFloat(
        label = "textSize",
        targetValueByState = { if (it) TAG_TEXT_SIZE_EXPANDED else TAG_TEXT_SIZE_SMALL }
    )
    val cornerRadius by transition.animateDp(
        label = "cardCorner",
        targetValueByState = { if (it) CornerLarge else CornerMedium }
    )
    val textPadding by transition.animateDp(
        label = "textPadding",
        targetValueByState = { if (it) SpacingMedium else SpacingSmall }
    )
    val progressIndicatorWidth by transition.animateDp(
        label = "progressIndicatorWidth",
        targetValueByState = { if (it) 24.dp else 12.dp }
    )
    val animatedPercent by animateFloatAsState(percentOfExpenditure)

    Surface(
        modifier = modifier
            .width(width),
        shape = RoundedCornerShape(cornerRadius),
        onClick = onClick,
        color = color,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .weight(WEIGHT_1)
                    .padding(horizontal = SpacingMedium, vertical = SpacingSmall)
            ) {
                Text(
                    text = name,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                    modifier = Modifier
                        .padding(top = textPadding)
                )
                Spacer(Modifier.height(SpacingSmall))
                Text(
                    text = stringResource(
                        R.string.percent_of_total,
                        TextUtil.formatPercent(animatedPercent)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(SpacingSmall))
                AnimatedVisibility(visible = expanded) {
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            if (!isUntagged) {
                AnimatedVisibility(
                    visible = expanded,
                    modifier = Modifier
                        .align(Alignment.Top)
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Outlined.DoNotDisturbOn,
                            contentDescription = stringResource(
                                R.string.content_description_delete_tag
                            ),
                            tint = contentColor.copy(alpha = ContentAlpha.PERCENT_16)
                        )
                    }
                }
            }
            VerticalProgressIndicator(
                progress = animatedPercent,
                width = progressIndicatorWidth,
                modifier = Modifier
                    .fillMaxHeight(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = ContentAlpha.PERCENT_32),
                indicatorColor = color.copy(alpha = ContentAlpha.PERCENT_60)
            )
        }
    }
}

private val TagOverviewHeight = 160.dp
private val TagOverviewCardBaseWidth = 120.dp
private const val TAG_TEXT_SIZE_SMALL = 16f
private const val TAG_TEXT_SIZE_EXPANDED = 20f

@Composable
private fun TotalExpenditure(
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMedium)
            .padding(top = SpacingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.total_spent),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth(0.60f)
        )
        Crossfade(targetState = amount) {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DateSelector(
    yearsList: List<String>,
    selectedYear: String?,
    onYearSelect: (String) -> Unit,
    selectedMonth: Int,
    onMonthSelect: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        YearSelector(
            yearsList = yearsList,
            selectedYear = selectedYear,
            onYearSelect = onYearSelect
        )
        val monthsList = remember { Month.values().toList() }
        MonthSelector(
            monthsList = monthsList,
            selectedMonth = selectedMonth,
            onMonthSelect = onMonthSelect
        )
    }
}

@Composable
private fun YearSelector(
    yearsList: List<String>,
    selectedYear: String?,
    onYearSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    Column(
        modifier = modifier
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = SpacingMedium)
                .clip(MaterialTheme.shapes.small)
                .clickable { expanded = !expanded }
                .padding(SpacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedYear.orEmpty(),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(SpacingXXSmall))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.content_description_toggle_dropdown),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = iconRotation
                    }
            )
        }
        if (expanded) {
            LazyRow(
                contentPadding = PaddingValues(
                    start = SpacingMedium,
                    end = ScrollEndPadding
                )
            ) {
                items(yearsList, key = { it }) { year ->
                    val selected = year == selectedYear
                    val alpha by animateFloatAsState(
                        targetValue = if (selected) Constants.ONE_F else ContentAlpha.PERCENT_32
                    )
                    Surface(
                        onClick = { onYearSelect(year) },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .animateItemPlacement()
                    ) {
                        Text(
                            text = year,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(SpacingXSmall)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    monthsList: List<Month>,
    selectedMonth: Int,
    onMonthSelect: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = SpacingMedium,
            end = ScrollEndPadding
        )
    ) {
        items(monthsList, key = { it.value }) { month ->
            MonthItem(
                month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                selected = month.value == selectedMonth,
                onClick = { onMonthSelect(month) }
            )
        }
    }
}

@Composable
private fun MonthItem(
    month: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = selected, label = "monthSelection")
    val alpha by transition.animateFloat(
        label = "monthAlpha",
        targetValueByState = { if (it) Constants.ONE_F else ContentAlpha.PERCENT_32 }
    )
    val elevation by transition.animateDp(
        label = "monthElevation",
        targetValueByState = { if (it) ElevationLevel1 else ZeroDp }
    )

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        tonalElevation = elevation,
        color = Color.Transparent,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 40.dp, minHeight = 32.dp)
                .padding(horizontal = SpacingXSmall),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = month,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(SpacingXSmall)
            )
        }
    }
}

@Composable
private fun ExpenseListItem(
    note: String,
    date: String,
    amount: String,
    selected: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = {
                    if (isClickable) onClick()
                },
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else Color.Transparent
        )
    ) {
        BaseExpenseCardLayout(
            note = note,
            date = date,
            amount = amount,
            tag = null
        )
    }
}

@Composable
private fun MultiSelectionOptions(
    selectionState: ToggleableState,
    onSelectionStateChange: (ToggleableState) -> Unit,
    onDeleteClick: () -> Unit,
    onUntagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = stringResource(R.string.content_description_delete_expense)
            )
        }
        IconButton(onClick = onUntagClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_untag),
                contentDescription = stringResource(R.string.content_description_untag_expenses)
            )
        }
        TriStateCheckbox(
            state = selectionState,
            onClick = { onSelectionStateChange(selectionState) })
    }
}