package io.thernal.overlaykit.overlay.components.sheet

import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

/** The fling speed, per second, past which a released sheet follows the fling — foundation's own. */
internal val SheetMinFlingVelocity = 125.dp

/**
 * Where a released sheet goes: a fling faster than [minFlingVelocityPx] goes its way, a slower
 * release closes the sheet once it was dragged past [dismissThreshold] of its height.
 */
internal fun sheetSettleTarget(
    offsetPx: Float,
    heightPx: Float,
    velocityPx: Float,
    minFlingVelocityPx: Float,
    dismissThreshold: Float,
): SheetValue {
    return when {
        velocityPx > minFlingVelocityPx -> SheetValue.Hidden
        velocityPx < -minFlingVelocityPx -> SheetValue.Expanded
        heightPx > 0f && offsetPx > heightPx * dismissThreshold -> SheetValue.Hidden
        else -> SheetValue.Expanded
    }
}

/**
 * Lets scrollable content inside a sheet hand over to the sheet: a list scrolled to its top drags
 * the sheet down, a sheet pulled down part way goes back up before the list scrolls, and a fling
 * that ends at the list's top carries on into the sheet — what Material's sheets do, missing from
 * the source app.
 */
@Stable
internal class SheetNestedScrollConnection(
    private val state: BottomSheetState,
    private val minFlingVelocityPx: () -> Float,
    private val dismissThreshold: () -> Float,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val delta = available.y
        if (delta < 0f && source == NestedScrollSource.UserInput) {
            return Offset(x = 0f, y = state.draggable.dispatchRawDelta(delta))
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source == NestedScrollSource.UserInput) {
            return Offset(x = 0f, y = state.draggable.dispatchRawDelta(available.y))
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val offset = state.draggable.offset
        if (available.y < 0f && !offset.isNaN() && offset > 0f) {
            settle(velocityPx = available.y)
            return available
        }
        return Velocity.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        settle(velocityPx = available.y)
        return available
    }

    private suspend fun settle(velocityPx: Float) {
        val height = state.sheetHeightPx ?: return
        val offset = state.draggable.offset
        if (offset.isNaN() || state.isDismissing) {
            return
        }
        state.draggable.animateTo(
            targetValue = sheetSettleTarget(
                offsetPx = offset,
                heightPx = height,
                velocityPx = velocityPx,
                minFlingVelocityPx = minFlingVelocityPx(),
                dismissThreshold = dismissThreshold(),
            ),
        )
    }
}
