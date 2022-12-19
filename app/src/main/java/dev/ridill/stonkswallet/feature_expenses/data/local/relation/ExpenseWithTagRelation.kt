package dev.ridill.stonkswallet.feature_expenses.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.TagEntity

data class ExpenseWithTagRelation(
    @Embedded val expenseEntity: ExpenseEntity,
    @Relation(
        entity = TagEntity::class,
        entityColumn = "tagName",
        parentColumn = "tag"
    ) val tagEntity: TagEntity?
)