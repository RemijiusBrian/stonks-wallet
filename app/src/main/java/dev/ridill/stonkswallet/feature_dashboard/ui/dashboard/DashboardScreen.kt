package dev.ridill.stonkswallet.feature_dashboard.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.BottomBarScreenSpec
import dev.ridill.stonkswallet.core.ui.theme.*
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.ui.util.verticalSpinner
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.partOfDay
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.presentation.components.ExpenseCard
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@Composable
fun DashboardScreen(
    state: DashboardState,
    snackbarController: SnackbarController,
    actions: DashboardActions,
    navigateToBottomBarDestination: (BottomBarScreenSpec) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Scaffold(
        snackbarHost = { DefaultSnackbarHost(snackbarController) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    BottomBarScreenSpec.screens.forEach { screen ->
                        IconButton(
                            onClick = {
                                navigateToBottomBarDestination(screen)
                            }
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.label)
                            )
                        }
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(visible = state.isLimitSet) {
                        FloatingActionButton(onClick = actions::onAddFabClick) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(R.string.content_description_add_expense)
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = ZeroDp
            )
        }
    ) { paddingValues ->
        val lazyListState = rememberLazyListState()
        val showScrollUpButton by remember {
            derivedStateOf { lazyListState.firstVisibleItemIndex > 2 }
        }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLimitSet) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(
                        start = SpacingMedium,
                        end = SpacingMedium,
                        bottom = ScrollEndPadding,
                        top = SpacingMedium
                    ),
                    verticalArrangement = Arrangement.spacedBy(SpacingMedium),
                    state = lazyListState
                ) {
                    item(key = "greeting") {
                        Greeting()
                    }
                    item(key = "Overview") {
                        Overview(
                            expenditureLimit = state.expenditureLimit,
                            expenditure = state.expenditure,
                            balance = state.balance,
                            balancePercent = state.balancePercent,
                            showBalanceWarning = state.showLowBalanceWarning,
                            modifier = Modifier
                                .heightIn(max = OverviewMaxHeight)
                        )
                    }

                    if (state.expenses.isNotEmpty()) {
                        item(key = "Expense List Label") {
                            ListLabel(label = R.string.expense_list_label)
                        }
                        items(items = state.expenses, key = { it.id }) { expense ->
                            ExpenseItem(
                                name = expense.note,
                                date = expense.dateFormatted,
                                amount = expense.amountFormatted,
                                onClick = { actions.onExpenseClick(expense.id) },
                                tag = expense.tag,
                                isPartOfPaymentPlan = expense.isPartOfPaymentPlan(),
                                onSwipe = { actions.onExpenseSwipe(expense) },
                                modifier = Modifier
                                    .animateItemPlacement()
                            )
                        }
                    }
                }
            } else {
                // TODO: Implement proper empty screen UI
            }
            AnimatedVisibility(
                visible = showScrollUpButton,
                modifier = Modifier
                    .padding(horizontal = SpacingMedium)
                    .align(Alignment.BottomEnd),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FilledTonalIconButton(onClick = {
                    coroutineScope.launch {
                        if (lazyListState.isScrollInProgress) {
                            lazyListState.scrollToItem(0)
                        } else {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.content_description_scroll_to_top)
                    )
                }
            }
        }
    }
}

@Composable
private fun Greeting(modifier: Modifier = Modifier) {
    val timeOfDay = rememberTimeOfDay()
    Text(
        text = stringResource(R.string.app_greeting, stringResource(timeOfDay)),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
private fun Overview(
    expenditureLimit: Long,
    expenditure: Double,
    balance: Double,
    balancePercent: Float,
    showBalanceWarning: Boolean,
    modifier: Modifier = Modifier
) {
    val balanceStartColor = MaterialTheme.colorScheme.secondary
    val errorColor = MaterialTheme.colorScheme.errorContainer
    val balancePercentAnimated by animateFloatAsState(targetValue = balancePercent)
    val vectorConverter = remember { Color.VectorConverter(ColorSpaces.Srgb) }
    val balanceColorAnimation = remember {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = vectorConverter,
            initialValue = errorColor,
            targetValue = balanceStartColor,
            initialVelocity = balanceStartColor
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OverviewStat(
            title = R.string.your_limit,
            modifier = Modifier
                .weight(Constants.ONE_F),
            titleStyle = MaterialTheme.typography.headlineMedium,
            valueStyle = MaterialTheme.typography.headlineLarge
        ) {
            AnimatedContent(
                targetState = expenditureLimit,
                transitionSpec = { verticalSpinner() }
            ) { limit ->
                Text(TextUtil.formatAmountWithCurrency(limit))
            }
        }
        Spacer(Modifier.width(SpacingSmall))
        Column(
            modifier = Modifier
                .weight(Constants.ONE_F)
        ) {
            OverviewStat(
                title = R.string.your_expenditure,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(Constants.ONE_F)
            ) {
                AnimatedContent(
                    targetState = expenditure,
                    transitionSpec = { verticalSpinner() }
                ) { expenditure ->
                    Text(TextUtil.formatAmountWithCurrency(expenditure))
                }
            }
            Spacer(Modifier.height(SpacingSmall))
            OverviewStat(
                title = R.string.balance,
                color = Color.Transparent,
                contentColor = contentColorFor(
                    backgroundColor = if (balancePercent <= 0.5) balanceColorAnimation.initialValue
                    else balanceColorAnimation.targetValue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(Constants.ONE_F)
                    .drawBehind {
                        val rectColor = balanceColorAnimation.getValueFromNanos(
                            (balanceColorAnimation.durationNanos * balancePercentAnimated.roundToLong())
                        )
                        drawRoundRect(
                            color = rectColor.copy(alpha = ContentAlpha.PERCENT_16),
                            cornerRadius = CornerRadius(CornerMedium.toPx())
                        )
                        drawRoundRect(
                            color = rectColor,
                            size = size.copy(
                                width = size.width * balancePercentAnimated
                            ),
                            cornerRadius = CornerRadius(CornerMedium.toPx())
                        )
                    }
            ) {
                AnimatedContent(
                    targetState = balance,
                    transitionSpec = { verticalSpinner() }
                ) { balance ->
                    Row {
                        Text(TextUtil.formatAmountWithCurrency(balance))
                        Spacer(Modifier.width(SpacingXSmall))
                        AnimatedVisibility(
                            visible = showBalanceWarning,
                            modifier = Modifier
                                .align(Alignment.Top)
                        ) {
                            BalanceLowWarning()
                        }
                    }
                }
            }
        }
    }
}

private val OverviewMaxHeight = 160.dp

@Composable
private fun OverviewStat(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    valueStyle: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = contentColorFor(backgroundColor = color),
    value: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = color,
        modifier = modifier,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingSmall),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(title),
                style = titleStyle
            )
            Spacer(Modifier.height(SpacingXSmall))
            CompositionLocalProvider(LocalTextStyle provides valueStyle) {
                value()
            }
        }
    }
}

@Composable
private fun BalanceLowWarning(
    modifier: Modifier = Modifier
) {
    val flashingAnim = rememberInfiniteTransition()
    val alpha = flashingAnim.animateFloat(
        initialValue = Constants.ONE_F,
        targetValue = ContentAlpha.PERCENT_32,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = WARNING_FLASH_DELAY),
            repeatMode = RepeatMode.Reverse
        )
    )
    Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = stringResource(R.string.content_description_balance_low_warning),
        tint = Color.Yellow.copy(alpha = alpha.value),
        modifier = modifier
            .size(SmallIconSize)
    )
}

private const val WARNING_FLASH_DELAY = 1000

@Composable
private fun ExpenseItem(
    name: String,
    date: String,
    amount: String,
    tag: Tag?,
    isPartOfPaymentPlan: Boolean,
    onClick: () -> Unit,
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeToDismiss(
        onSwipe = onSwipe,
        modifier = modifier,
        background = { SwipeDeleteBackground() },
        direction = SwipeDirection.LEFT_TO_RIGHT
    ) {
        ExpenseCard(
            note = name,
            date = date,
            amount = amount,
            tag = tag,
            billExpense = isPartOfPaymentPlan,
            onClick = onClick
        )
    }
}

@Composable
private fun rememberTimeOfDay(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Int {
    var timeOfDay by remember { mutableStateOf(R.string.hour_morning) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                timeOfDay = DateUtil.currentDateTime().partOfDay
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return timeOfDay
}