package dev.ridill.stonkswallet.feature_expenses.data.mapper

import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.TagWithExpenditureRelation
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.domain.model.TagOverview

fun TagEntity.toTag(): Tag = Tag(
    name = tagName,
    colorCode = colorCode
)

fun Tag.toEntity(): TagEntity = TagEntity(
    tagName = name,
    colorCode = colorCode
)

fun TagWithExpenditureRelation.toTagOverview(
    totalExpenditure: Double
): TagOverview {
    val tagObj = if (colorCode == null) Tag.Untagged
    else Tag(tag, colorCode)
    return TagOverview(
        tag = tagObj.name,
        color = tagObj.color,
        amount = TextUtil.formatAmountWithCurrency(expenditure),
        percentOfLimit = (expenditure / totalExpenditure).toFloat()
    )
}