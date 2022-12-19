package dev.ridill.stonkswallet.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import dev.ridill.stonkswallet.core.ui.theme.SpacingLarge
import dev.ridill.stonkswallet.core.util.Constants
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SwipeToDismiss(
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable (BoxScope.() -> Unit)? = null,
    direction: SwipeDirection = SwipeDirection.BI_DIRECTIONAL,
    threshold: Float = DEFAULT_SWIPE_THRESHOLD,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetXAnimation = remember { Animatable(0f) }
    var width by remember { mutableStateOf(0) }
    LaunchedEffect(direction, width) {
        when (direction) {
            SwipeDirection.LEFT_TO_RIGHT -> {
                offsetXAnimation.updateBounds(-SWIPE_PADDING, width.toFloat())
            }
            SwipeDirection.RIGHT_TO_LEFT -> {
                offsetXAnimation.updateBounds(-width.toFloat(), SWIPE_PADDING)
            }
            SwipeDirection.BI_DIRECTIONAL -> {
                offsetXAnimation.updateBounds(-width.toFloat(), width.toFloat())
            }
        }
    }
    val draggableState = rememberDraggableState { delta ->
        coroutineScope.launch {
            offsetXAnimation.snapTo(offsetXAnimation.value + delta)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { width = it.size.width }
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        if (offsetXAnimation.value.absoluteValue < (width * threshold)) {
                            offsetXAnimation.springTo(Constants.ZERO_F)
                        } else {
                            offsetXAnimation.springTo(
                                (if (offsetXAnimation.value < 0) -1f else 1f) * width.toFloat(),
                                velocity
                            )
                            onSwipe()
                        }
                    }
                }
            )
    ) {
        background?.invoke(this)
        Box(
            modifier = Modifier
                .offset { IntOffset(x = offsetXAnimation.value.roundToInt(), y = 0) }
        ) { content() }
    }
}

private suspend fun <T, V : AnimationVector> Animatable<T, V>.springTo(
    targetValue: T,
    velocity: T = typeConverter.convertFromVector(velocityVector)
) {
    animateTo(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        initialVelocity = velocity
    )
}

enum class SwipeDirection { LEFT_TO_RIGHT, RIGHT_TO_LEFT, BI_DIRECTIONAL }

private const val SWIPE_PADDING = 80f
private const val DEFAULT_SWIPE_THRESHOLD = 0.4f

@Composable
fun BoxScope.SwipeDeleteBackground(
    modifier: Modifier = Modifier
) {
    val errorContainerColor = MaterialTheme.colorScheme.errorContainer
    Row(
        modifier = modifier
            .matchParentSize()
            .clip(MaterialTheme.shapes.medium)
            .drawBehind {
                drawRect(errorContainerColor)
            }
            .padding(horizontal = SpacingLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
    }
}