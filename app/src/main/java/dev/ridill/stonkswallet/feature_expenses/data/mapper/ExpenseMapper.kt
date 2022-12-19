package dev.ridill.stonkswallet.feature_expenses.data.mapper

import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.ExpenseWithTagRelation
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense

fun ExpenseEntity.toExpense(): Expense = Expense(
    id = id,
    note = note,
    amount = amount,
    dateMillis = dateMillis,
    tag = null,
    paymentPlanId = paymentPlanId
)

fun ExpenseWithTagRelation.toExpense(): Expense = Expense(
    id = expenseEntity.id,
    note = expenseEntity.note,
    amount = expenseEntity.amount,
    dateMillis = expenseEntity.dateMillis,
    tag = tagEntity?.toTag(),
    paymentPlanId = expenseEntity.paymentPlanId
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    note = note,
    amount = amount,
    dateMillis = dateMillis,
    tag = tag?.name,
    paymentPlanId = paymentPlanId
)