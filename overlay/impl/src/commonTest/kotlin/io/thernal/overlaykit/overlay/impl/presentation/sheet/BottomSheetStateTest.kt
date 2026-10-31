package io.thernal.overlaykit.overlay.impl.presentation.sheet

import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.impl.presentation.modal.ImmediateFrameClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BottomSheetStateTest {

    @Test
    fun `a swap forgets the last height and gives the next sheet a layout of its own`() {
        runTest(context = ImmediateFrameClock()) {
            val state = BottomSheetState(animationMillis = ANIMATION_MILLIS)
            state.onTopEntryChanged(topKey = "a", topEntry = entry())
            state.onSheetHeightChanged(heightPx = HEIGHT_PX)
            val before = state.entryGeneration

            state.onTopEntryChanged(topKey = "b", topEntry = entry())

            assertNull(state.sheetHeightPx)
            assertEquals(before + 1, state.entryGeneration)
        }
    }

    @Test
    fun `republishing the sheet on screen keeps its layout and its height`() {
        runTest(context = ImmediateFrameClock()) {
            val state = BottomSheetState(animationMillis = ANIMATION_MILLIS)
            state.onTopEntryChanged(topKey = "a", topEntry = entry())
            state.onSheetHeightChanged(heightPx = HEIGHT_PX)
            val before = state.entryGeneration

            state.syncTopEntry(topKey = "a", topEntry = entry())

            assertEquals(HEIGHT_PX, state.sheetHeightPx)
            assertEquals(before, state.entryGeneration)
        }
    }

    private fun entry(): BottomSheetEntry {
        return BottomSheetEntry(
            onDismissRequest = {},
            isDismissibleOutside = true,
            isDismissibleByBack = true,
            isDraggable = true,
            style = BottomSheetStyle(),
            content = {},
        )
    }

    private companion object {
        const val ANIMATION_MILLIS = 300
        const val HEIGHT_PX = 400f
    }
}
