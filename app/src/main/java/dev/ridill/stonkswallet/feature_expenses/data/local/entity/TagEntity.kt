package dev.ridill.stonkswallet.feature_expenses.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = false)
    val tagName: String,
    val colorCode: Int
)