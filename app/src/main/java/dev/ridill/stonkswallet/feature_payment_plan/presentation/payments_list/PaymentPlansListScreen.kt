package dev.ridill.stonkswallet.feature_payment_plan.presentation.payments_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.*
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.PaymentPlansListScreenSpec
import dev.ridill.stonkswallet.core.ui.theme.ScrollEndPadding
import dev.ridill.stonkswallet.core.ui.theme.SpacingMedium
import dev.ridill.stonkswallet.core.ui.theme.SpacingSmall
import dev.ridill.stonkswallet.core.ui.theme.WEIGHT_1
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanListItem
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentStatus
import dev.ridill.stonkswallet.feature_payment_plan.presentation.components.PaymentPlanCategoryIcon

@Composable
fun PaymentPlansListScreenContent(
    state: PaymentsListScreenState,
    snackbarController: SnackbarController,
    navigateToAddEditPaymentPlanScreen: () -> Unit,
    actions: PaymentPlansListActions,
    navigateUp: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val isFabExpanded by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex < 1 }
    }

    Scaffold(
        snackbarHost = { DefaultSnackbarHost(snackbarController) },
        topBar = {
            TransparentTopAppBar(
                title = PaymentPlansListScreenSpec.label,
                navigationIcon = { BackArrowButton(onClick = navigateUp) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = navigateToAddEditPaymentPlanScreen,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null
                    )
                },
                text = { Text(stringResource(R.string.new_payment_plan)) },
                expanded = isFabExpanded
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                top = SpacingMedium,
                bottom = ScrollEndPadding
            ),
            verticalArrangement = Arrangement.spacedBy(SpacingMedium)
        ) {
            item(key = "BillsGrid") {
                BillsGrid(
                    bills = state.billsList,
                    onBillClick = actions::onPlanClick
                )
            }
            state.paymentsPlanExpense.forEach { (state, payments) ->
                item(key = state.name) {
                    ListLabel(
                        label = state.label,
                        modifier = Modifier
                            .padding(horizontal = SpacingSmall)
                            .animateItemPlacement()
                    )
                }
                items(items = payments, key = { it.billId }) { payment ->
                    BillPayment(
                        category = payment.category,
                        name = payment.name,
                        date = payment.dateFormatted,
                        amount = payment.amountFormatted,
                        status = state,
                        onMarkAsPaidClick = { actions.onMarkAsPaidClick(payment) },
                        modifier = Modifier
                            .animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
private fun BillsGrid(
    bills: Map<PaymentCategory, List<PaymentPlanListItem>>,
    onBillClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BillsGridHeight),
        contentAlignment = Alignment.Center
    ) {
        if (bills.isEmpty()) {
            // TODO: Implement no data message
        } else {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .matchParentSize(),
                contentPadding = PaddingValues(
                    end = ScrollEndPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(SpacingSmall),
                verticalArrangement = Arrangement.spacedBy(SpacingSmall)
            ) {
                bills.forEach { (category, list) ->
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        key = category.name,
                        contentType = PaymentCategory::class.java
                    ) {
                        BillSeparator(
                            category = category,
                            modifier = Modifier
                                .animateItemPlacement()
                        )
                    }
                    items(
                        items = list,
                        key = { it.id },
                        contentType = { PaymentPlanListItem::class.java }
                    ) { bill ->
                        BillCard(
                            name = bill.name,
                            modifier = Modifier
                                .animateItemPlacement(),
                            onClick = { onBillClick(bill.id) },
                            dueDate = bill.dueDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillSeparator(
    category: PaymentCategory,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .rotate(-90f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = category.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.width(SpacingMedium))
        Icon(
            imageVector = ImageVector.vectorResource(category.icon),
            contentDescription = category.label,
            modifier = Modifier
                .rotate(90f)
        )
    }
}

@Composable
private fun BillCard(
    name: String,
    onClick: () -> Unit,
    dueDate: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .widthIn(max = BillCardMaxWidth),
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.due_date_value, dueDate),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BillPayment(
    category: PaymentCategory,
    name: String,
    amount: String,
    date: String,
    status: PaymentStatus,
    onMarkAsPaidClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingSmall)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaymentPlanCategoryIcon(category = category)
                Spacer(Modifier.width(SpacingMedium))
                Column(
                    modifier = Modifier
                        .weight(WEIGHT_1)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.text_in_parenthesis, category.label),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = stringResource(status.displayMessage, date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.width(SpacingMedium))
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (status != PaymentStatus.PAID) {
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    TextButton(onClick = onMarkAsPaidClick) {
                        Text(stringResource(R.string.mark_as_paid))
                    }
                }
            }
        }
    }
}

private val BillsGridHeight = 172.dp
private val BillCardMaxWidth = 160.dp