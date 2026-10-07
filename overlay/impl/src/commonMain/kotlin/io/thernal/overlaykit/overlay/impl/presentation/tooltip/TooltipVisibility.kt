package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipParams

/**
 * Whether a tooltip shows: [TooltipParams.isVisible] when the caller controls it, its own state
 * otherwise. Every change is reported to [TooltipParams.onVisibilityChange]; [showToken] grows with
 * each opening by the anchor, so the balloon pops again.
 */
internal class TooltipVisibility(
    private val params: State<TooltipParams>,
) {
    private var isOpenInternally by mutableStateOf(false)

    var showToken by mutableIntStateOf(0)
        private set

    val isShown: Boolean
        get() = params.value.isVisible ?: isOpenInternally

    fun update(isVisible: Boolean) {
        if (params.value.isVisible == null) {
            isOpenInternally = isVisible
        }
        params.value.onVisibilityChange?.invoke(isVisible)
    }

    fun toggle() {
        val isNextVisible = !isShown
        if (isNextVisible) {
            showToken += 1
        }
        update(isVisible = isNextVisible)
    }
}

@Composable
internal fun rememberTooltipVisibility(params: TooltipParams): TooltipVisibility {
    val latest = rememberUpdatedState(params)
    return remember { TooltipVisibility(latest) }
}
