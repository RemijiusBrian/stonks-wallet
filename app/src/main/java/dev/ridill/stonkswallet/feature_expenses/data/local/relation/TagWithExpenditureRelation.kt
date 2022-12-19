package dev.ridill.stonkswallet.feature_expenses.data.local.relation

data class TagWithExpenditureRelation(
    val tag: String,
    val colorCode: Int?,
    val expenditure: Double
)
