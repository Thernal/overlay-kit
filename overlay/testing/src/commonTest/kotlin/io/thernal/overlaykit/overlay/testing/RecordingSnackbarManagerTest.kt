package io.thernal.overlaykit.overlay.testing

import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarKind
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecordingSnackbarManagerTest {

    @Test
    fun `records every message in order and tracks the current one`() {
        val manager = RecordingSnackbarManager()

        manager.show(SnackbarMessage(text = "Saved", kind = SnackbarKind.Success))
        manager.show(SnackbarMessage(text = "Offline", kind = SnackbarKind.Warning))

        assertEquals(listOf("Saved", "Offline"), manager.messages.map { it.text })
        assertEquals("Offline", manager.current?.text)
        assertTrue(manager.isVisible)
    }

    @Test
    fun `dismiss clears the current message and is counted`() {
        val manager = RecordingSnackbarManager()
        manager.show(SnackbarMessage(text = "Saved"))

        manager.dismiss()

        assertFalse(manager.isVisible)
        assertEquals(1, manager.dismissCount)
        assertEquals(1, manager.messages.size)
    }
}
