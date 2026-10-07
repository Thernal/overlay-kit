package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarManager
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Shorter than the exit animation on purpose: the queue moves on while the previous message is
// still sliding out, so the next one enters during the exit instead of after it.

/**
 * The queue behind [SnackbarManager]. Commands are processed in order by [run], which the plugin
 * runs for as long as it is composed — the timers live in that coroutine and die with it, instead
 * of in a scope of the controller's own that someone has to remember to cancel.
 *
 * A press on the message holds its timer; the time left resumes on release.
 */
@Stable
class SnackbarController(
    var defaultDurationMillis: Long,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) : SnackbarManager {
    override var current: SnackbarMessage? by mutableStateOf(null)
        private set

    override var isVisible: Boolean by mutableStateOf(false)
        private set

    private val queue = ArrayDeque<SnackbarMessage>()
    private val commands = Channel<SnackbarCommand>(capacity = Channel.UNLIMITED)
    private var timerScope: CoroutineScope? = null
    private var autoDismissJob: Job? = null
    private var remainingMillis: Long = 0L
    private var timerStart: TimeMark? = null
    private var isUserInteracting: Boolean = false

    override fun show(message: SnackbarMessage) {
        commands.trySend(SnackbarCommand.Enqueue(message))
    }

    override fun dismiss() {
        commands.trySend(SnackbarCommand.Dismiss(target = null))
    }

    fun onInteractionChanged(isInteracting: Boolean) {
        commands.trySend(SnackbarCommand.SetUserInteracting(isInteracting))
    }

    suspend fun run() {
        coroutineScope {
            timerScope = this
            try {
                for (command in commands) {
                    handle(command)
                }
            } finally {
                cancelAutoDismissTimer()
                timerScope = null
            }
        }
    }

    private suspend fun handle(command: SnackbarCommand) {
        when (command) {
            is SnackbarCommand.Enqueue -> {
                if (current == null) {
                    showInternal(command.message)
                } else {
                    queue.addLast(command.message)
                }
            }

            is SnackbarCommand.Dismiss -> {
                // A timer's dismiss names its message: one that fired just as the message was
                // closed by hand must not close the next one.
                if (command.target == null || command.target === current) {
                    dismissCurrentInternal()
                }
            }

            is SnackbarCommand.SetUserInteracting -> setUserInteracting(command.isInteracting)
        }
    }

    private fun showInternal(message: SnackbarMessage) {
        cancelAutoDismissTimer()
        isUserInteracting = false
        remainingMillis = message.durationMillis ?: defaultDurationMillis
        current = message
        isVisible = true
        scheduleAutoDismissIfNeeded()
    }

    private fun scheduleAutoDismissIfNeeded() {
        val scope = timerScope ?: return
        if (current == null || isUserInteracting) {
            return
        }
        val message = current ?: return
        if (remainingMillis <= 0L) {
            commands.trySend(SnackbarCommand.Dismiss(target = message))
            return
        }

        timerStart = timeSource.markNow()
        val delayMillis = remainingMillis
        autoDismissJob = scope.launch {
            delay(delayMillis)
            commands.send(SnackbarCommand.Dismiss(target = message))
        }
    }

    private fun cancelAutoDismissTimer() {
        autoDismissJob?.cancel()
        autoDismissJob = null
        timerStart = null
    }

    private fun setUserInteracting(isInteracting: Boolean) {
        if (current == null || isUserInteracting == isInteracting) {
            return
        }

        isUserInteracting = isInteracting
        if (isInteracting) {
            val elapsedMillis = timerStart?.elapsedNow()?.inWholeMilliseconds?.coerceAtLeast(0L) ?: 0L
            remainingMillis = (remainingMillis - elapsedMillis).coerceAtLeast(0L)
            cancelAutoDismissTimer()
        } else {
            scheduleAutoDismissIfNeeded()
        }
    }

    private suspend fun dismissCurrentInternal() {
        val dismissed = current ?: return
        cancelAutoDismissTimer()
        isUserInteracting = false
        isVisible = false
        delay(DISMISS_ANIMATION_DELAY_MILLIS)
        dismissed.onDismiss?.invoke()
        current = null

        val next = queue.removeFirstOrNull() ?: return
        delay(QUEUE_DELAY_MILLIS)
        showInternal(next)
    }
}

private sealed interface SnackbarCommand {
    data class Enqueue(val message: SnackbarMessage) : SnackbarCommand

    data class SetUserInteracting(val isInteracting: Boolean) : SnackbarCommand

    class Dismiss(val target: SnackbarMessage?) : SnackbarCommand
}

private const val DISMISS_ANIMATION_DELAY_MILLIS = 300L
private const val QUEUE_DELAY_MILLIS = 100L
