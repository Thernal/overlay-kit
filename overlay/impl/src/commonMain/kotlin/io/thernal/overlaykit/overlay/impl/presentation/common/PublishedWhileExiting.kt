package io.thernal.overlaykit.overlay.impl.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * True from the moment an anchored overlay shows until its exit has run: the entry stays on the stack
 * while it fades out, so the host can draw the exit. The tooltip's and the showcase's.
 */
@Composable
internal fun rememberPublishedWhileExiting(
    isShown: Boolean,
    exitMillis: Int,
): Boolean {
    var isPublished by remember { mutableStateOf(false) }
    LaunchedEffect(isShown) {
        if (isShown) {
            isPublished = true
        } else {
            if (isPublished && exitMillis > 0) {
                delay(exitMillis.toLong())
            }
            isPublished = false
        }
    }
    return isPublished
}
