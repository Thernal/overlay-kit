package io.thernal.overlaykit.overlay.components.sheet

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.thernal.overlaykit.overlay.core.modal.ModalOverlayState

internal enum class SheetValue {
    Hidden,
    Expanded,
}

/**
 * A sheet's lifecycle on top of foundation's [AnchoredDraggableState]: two anchors — expanded at 0,
 * hidden at the sheet's height — and the drag, the fling, the nested scroll and the show and hide
 * animations all move the same offset. Progress is read from that offset, so the scrim and the
 * backdrop follow a finger exactly as they follow an animation.
 */
@Stable
internal class BottomSheetState(
    animationMillis: Int,
) : ModalOverlayState<BottomSheetEntry>() {
    val draggable: AnchoredDraggableState<SheetValue> = AnchoredDraggableState(initialValue = SheetValue.Hidden)

    var animationMillis: Int by mutableIntStateOf(animationMillis)

    var sheetHeightPx: Float? by mutableStateOf(null)
        private set

    // Set once the sheet has settled open; a later settle at Hidden that this state did not start
    // is the user dragging or flinging it shut.
    internal var hasSettledExpanded: Boolean = false

    override val progress: Float
        get() {
            val height = sheetHeightPx
            val offset = draggable.offset
            if (height == null || height <= 0f || offset.isNaN()) {
                return 0f
            }
            return (1f - offset / height).coerceIn(0f, 1f)
        }

    /** The sheet's vertical offset; hidden below the screen until its anchors exist. */
    fun offsetPx(measuredHeightPx: Float): Float {
        val offset = draggable.offset
        return if (offset.isNaN()) {
            measuredHeightPx
        } else {
            offset
        }
    }

    fun onSheetHeightChanged(heightPx: Float) {
        if (sheetHeightPx == heightPx) {
            return
        }
        sheetHeightPx = heightPx
        draggable.updateAnchors(
            newAnchors = DraggableAnchors {
                SheetValue.Hidden at heightPx
                SheetValue.Expanded at 0f
            },
            newTarget = draggable.targetValue,
        )
    }

    /** The user closed the sheet by hand; the exit is already done, only the lifecycle remains. */
    internal fun onClosedByUser() {
        hasSettledExpanded = false
        isDismissing = true
    }

    override suspend fun animateIn() {
        if (sheetHeightPx == null) {
            return
        }
        draggable.animateTo(targetValue = SheetValue.Expanded, animationSpec = tween(durationMillis = animationMillis))
    }

    override suspend fun animateOut() {
        if (sheetHeightPx == null) {
            return
        }
        draggable.animateTo(targetValue = SheetValue.Hidden, animationSpec = tween(durationMillis = animationMillis))
    }

    override suspend fun snapHidden() {
        hasSettledExpanded = false
        if (draggable.anchors.size > 0) {
            draggable.snapTo(targetValue = SheetValue.Hidden)
        }
    }

    // Another sheet is taking over; its height is unknown until it is measured.
    override fun onEntrySwapped() {
        sheetHeightPx = null
    }
}
