package dev.ridill.stonkswallet.feature_expenses.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity

data class TagWithExpenseRelation(
    @Embedded
    val tag: TagEntity,
    @Relation(
        entity = ExpenseEntity::class,
        parentColumn = "tagName",
        entityColumn = "tag"
    )
    val expenses: List<ExpenseEntity>
)