package io.thernal.overlaykit.overlay.components.snackbar

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val DURATION = 2_000L

@OptIn(ExperimentalCoroutinesApi::class)
class SnackbarControllerTest {

    private fun TestScope.startedController(): SnackbarController {
        val controller = SnackbarController(
            defaultDurationMillis = DURATION,
            timeSource = testScheduler.timeSource,
        )
        backgroundScope.launch { controller.run() }
        runCurrent()
        return controller
    }

    @Test
    fun `shows a message and hides it after its duration`() {
        runTest {
            val controller = startedController()

            controller.show(SnackbarMessage(text = "Saved"))
            runCurrent()

            assertEquals("Saved", controller.current?.text)
            assertTrue(controller.isVisible)

            advanceTimeBy(DURATION + 1)
            assertFalse(controller.isVisible)

            advanceTimeBy(1_000)
            assertNull(controller.current)
        }
    }

    @Test
    fun `queues messages and shows them in order`() {
        runTest {
            val controller = startedController()

            controller.show(SnackbarMessage(text = "First"))
            controller.show(SnackbarMessage(text = "Second"))
            runCurrent()
            assertEquals("First", controller.current?.text)

            advanceTimeBy(DURATION + 1_000)
            assertEquals("Second", controller.current?.text)
            assertTrue(controller.isVisible)
        }
    }

    @Test
    fun `a message's own duration wins over the default`() {
        runTest {
            val controller = startedController()

            controller.show(SnackbarMessage(text = "Long", durationMillis = 5_000))
            runCurrent()

            advanceTimeBy(DURATION + 1)
            assertTrue(controller.isVisible)

            advanceTimeBy(3_000)
            assertFalse(controller.isVisible)
        }
    }

    @Test
    fun `holding the message pauses its timer and releasing resumes the time left`() {
        runTest {
            val controller = startedController()
            controller.show(SnackbarMessage(text = "Hold me"))
            runCurrent()

            advanceTimeBy(1_500)
            controller.onInteractionChanged(isInteracting = true)
            runCurrent()
            advanceTimeBy(10_000)
            assertTrue(controller.isVisible)

            controller.onInteractionChanged(isInteracting = false)
            runCurrent()
            advanceTimeBy(400)
            assertTrue(controller.isVisible)

            advanceTimeBy(200)
            assertFalse(controller.isVisible)
        }
    }

    @Test
    fun `dismiss runs onDismiss and moves to the next message`() {
        runTest {
            val controller = startedController()
            var dismissedCount = 0
            controller.show(SnackbarMessage(text = "First", onDismiss = { dismissedCount++ }))
            controller.show(SnackbarMessage(text = "Second"))
            runCurrent()

            controller.dismiss()
            advanceTimeBy(1_000)

            assertEquals(1, dismissedCount)
            assertEquals("Second", controller.current?.text)
        }
    }

    @Test
    fun `a stale timer does not close the next message`() {
        runTest {
            val controller = startedController()
            controller.show(SnackbarMessage(text = "First"))
            controller.show(SnackbarMessage(text = "Second"))
            runCurrent()

            // Closed by hand just before its own timer would have fired.
            advanceTimeBy(DURATION - 1)
            controller.dismiss()
            advanceTimeBy(500)
            assertEquals("Second", controller.current?.text)

            advanceTimeBy(DURATION - 1_000)
            assertTrue(controller.isVisible)
        }
    }
}
