package io.thernal.overlaykit.overlay.impl.presentation.backdrop

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * How far the content behind the overlays is pushed back, 0 (untouched) to 1 (fully). Several
 * overlays may contribute at once — a sheet over a sheet — and the strongest wins.
 */
@Stable
class OverlayBackdropController {
    private val contributions = mutableStateMapOf<String, Float>()

    private val currentProgress = mutableFloatStateOf(0f)
    val progress: State<Float> = currentProgress

    fun setProgress(
        owner: String,
        progress: Float,
    ) {
        val safeProgress = progress.coerceIn(0f, 1f)
        if (contributions[owner] == safeProgress) {
            return
        }

        if (safeProgress == 0f) {
            contributions.remove(owner)
        } else {
            contributions[owner] = safeProgress
        }

        recalculateProgress()
    }

    fun clear(owner: String) {
        if (owner in contributions) {
            contributions.remove(owner)
            recalculateProgress()
        }
    }

    private fun recalculateProgress() {
        currentProgress.floatValue = contributions.values.maxOrNull()?.coerceIn(0f, 1f) ?: 0f
    }
}

val LocalOverlayBackdropController = staticCompositionLocalOf<OverlayBackdropController> {
    error("No OverlayBackdropController: wrap the app in OverlayHost (or ProvideOverlayControllers).")
}
