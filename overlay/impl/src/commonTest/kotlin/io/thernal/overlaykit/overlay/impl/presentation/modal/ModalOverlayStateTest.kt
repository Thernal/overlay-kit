package io.thernal.overlaykit.overlay.impl.presentation.modal

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class TestModalOverlayState : AnimatedModalOverlayState<String>(animationMillis = 300) {
    var entrySwappedCount = 0
        private set

    override fun onEntrySwapped() {
        entrySwappedCount++
    }

    suspend fun setProgressForTest(value: Float) {
        snapProgress(value)
    }
}

class ModalOverlayStateTest {

    @Test
    fun `shows a new entry without animating or touching progress`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()

            state.onTopEntryChanged(topKey = "a", topEntry = "Entry A")

            assertTrue(state.shouldRender)
            assertEquals("Entry A", state.currentEntry)
            assertFalse(state.isDismissing)
            assertEquals(0f, state.progress)
            assertEquals(0, state.entrySwappedCount)
        }
    }

    @Test
    fun `swapping to a different key resets progress and notifies onEntrySwapped`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()
            state.onTopEntryChanged(topKey = "a", topEntry = "Entry A")
            state.setProgressForTest(1f)

            state.onTopEntryChanged(topKey = "b", topEntry = "Entry B")

            assertEquals(1, state.entrySwappedCount)
            assertEquals(0f, state.progress)
            assertEquals("Entry B", state.currentEntry)
            assertTrue(state.shouldRender)
        }
    }

    @Test
    fun `dismissing animates progress back to zero and clears render state`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()
            state.onTopEntryChanged(topKey = "a", topEntry = "Entry A")
            state.setProgressForTest(1f)

            state.onTopEntryChanged(topKey = null, topEntry = null)

            assertEquals(0f, state.progress)
            assertFalse(state.shouldRender)
            assertNull(state.currentEntry)
            assertFalse(state.isDismissing)
        }
    }

    @Test
    fun `dismissing with nothing shown does nothing`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()

            state.onTopEntryChanged(topKey = null, topEntry = null)

            assertFalse(state.shouldRender)
            assertFalse(state.isDismissing)
        }
    }

    @Test
    fun `syncTopEntry only refreshes the entry when the key still matches`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()
            state.onTopEntryChanged(topKey = "a", topEntry = "Entry A")

            state.syncTopEntry(topKey = "a", topEntry = "Entry A refreshed")
            assertEquals("Entry A refreshed", state.currentEntry)

            state.syncTopEntry(topKey = "b", topEntry = "Entry B")
            assertEquals("Entry A refreshed", state.currentEntry)
        }
    }

    @Test
    fun `scrimAlpha scales with progress and clamps above the valid range`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()

            state.setProgressForTest(0.5f)
            assertEquals(0.3f, state.scrimAlpha(scrimAlpha = 0.6f))

            state.setProgressForTest(1f)
            assertEquals(1f, state.scrimAlpha(scrimAlpha = 1.5f))
        }
    }

    @Test
    fun `ensureHiddenWhenNotRendering resets stale progress only while not rendering`() {
        runTest(context = ImmediateFrameClock()) {
            val hidden = TestModalOverlayState()
            hidden.setProgressForTest(0.6f)

            hidden.ensureHiddenWhenNotRendering()

            assertEquals(0f, hidden.progress)

            val shown = TestModalOverlayState()
            shown.onTopEntryChanged(topKey = "a", topEntry = "Entry A")
            shown.setProgressForTest(0.6f)

            shown.ensureHiddenWhenNotRendering()

            assertEquals(0.6f, shown.progress)
        }
    }

    @Test
    fun `animateInIfReady animates to fully shown only when an entry is ready`() {
        runTest(context = ImmediateFrameClock()) {
            val withEntry = TestModalOverlayState()
            withEntry.onTopEntryChanged(topKey = "a", topEntry = "Entry A")

            withEntry.animateInIfReady()

            assertEquals(1f, withEntry.progress)

            val withoutEntry = TestModalOverlayState()

            withoutEntry.animateInIfReady()

            assertEquals(0f, withoutEntry.progress)
        }
    }

    @Test
    fun `animateInIfReady does nothing while the entry is leaving`() {
        runTest(context = ImmediateFrameClock()) {
            val state = TestModalOverlayState()
            state.onTopEntryChanged(topKey = "a", topEntry = "Entry A")
            state.onTopEntryChanged(topKey = null, topEntry = null)

            state.animateInIfReady()

            assertEquals(0f, state.progress)
        }
    }
}
