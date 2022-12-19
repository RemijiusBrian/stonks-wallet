package dev.ridill.stonkswallet.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Composable
fun SelectionRow(
    itemCount: Int,
    selectedIndex: Int,
    item: @Composable (Int) -> Unit,
    selectedIndicator: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    contentPadding: PaddingValues = PaddingValues()
) {
    val animatedIndex by animateFloatAsState(selectedIndex.toFloat())
    val measurePolicy = selectionRowMeasurePolicy(contentPadding, animatedIndex, layoutDirection)
    val itemComposables = @Composable { repeat(itemCount) { item(it) } }

    Layout(
        contents = listOf(selectedIndicator, itemComposables),
        measurePolicy = measurePolicy,
        modifier = modifier
            .horizontalScroll(scrollState)
    )
}

@Composable
private fun selectionRowMeasurePolicy(
    contentPadding: PaddingValues,
    selectedIndex: Float,
    layoutDirection: LayoutDirection
): MultiContentMeasurePolicy = remember(contentPadding, selectedIndex, layoutDirection) {
    MultiContentMeasurePolicy { measurables, constraints ->
        val indicatorMeasurables = measurables[0]
        require(indicatorMeasurables.size == 1) {
            "Exactly One indicator must be present"
        }

        // Padding Values
        val startPadding = contentPadding.calculateStartPadding(layoutDirection).toPx().roundToInt()
        val endPadding = contentPadding.calculateEndPadding(layoutDirection).toPx().roundToInt()
        val topPadding = contentPadding.calculateTopPadding().toPx().roundToInt()
        val bottomPadding = contentPadding.calculateBottomPadding().toPx().roundToInt()

        // Measure item composables and calculate total width
        var totalWidth = startPadding
        val itemPlaceables = measurables[1].map {
            val placeable = it.measure(constraints)
            totalWidth += placeable.width
            placeable
        }
        totalWidth += endPadding

        // Indicator Composable
        val itemHeight = itemPlaceables.maxOfOrNull { it.measuredHeight } ?: 0
        val itemWidth = itemPlaceables.maxOfOrNull { it.measuredWidth } ?: 0
        val indicatorPlaceable = indicatorMeasurables.firstOrNull()?.measure(
            constraints.copy(minHeight = itemHeight, minWidth = itemWidth)
        )
        val height = itemHeight + topPadding + bottomPadding

        layout(totalWidth, height) {
            var offsetX = when (layoutDirection) {
                LayoutDirection.Ltr -> startPadding
                LayoutDirection.Rtl -> totalWidth - startPadding
            }
            when (layoutDirection) {
                LayoutDirection.Ltr -> {
                    itemPlaceables.forEachIndexed { index, placeable ->
                        placeable.place(offsetX, topPadding)
                        if (indicatorPlaceable != null && index == selectedIndex.roundToInt()) {
                            indicatorPlaceable.place(offsetX, topPadding)
                        }
                        offsetX += placeable.width
                    }
                }
                LayoutDirection.Rtl -> {
                    itemPlaceables.forEachIndexed { index, placeable ->
                        offsetX -= placeable.width
                        placeable.place(offsetX, topPadding)
                        if (indicatorPlaceable != null && index == selectedIndex.roundToInt()) {
                            indicatorPlaceable.place(offsetX, topPadding)
                        }
                    }
                }
            }
        }
    }
}