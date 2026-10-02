package io.thernal.overlaykit.overlay.testing

import io.thernal.overlaykit.overlay.api.presentation.dialog.LocalDialogRenderer
import io.thernal.overlaykit.overlay.api.presentation.dropdown.LocalDropdownRenderer
import io.thernal.overlaykit.overlay.api.presentation.sheet.LocalBottomSheetRenderer
import io.thernal.overlaykit.overlay.api.presentation.showcase.LocalShowcaseRenderer
import io.thernal.overlaykit.overlay.api.presentation.tooltip.LocalTooltipRenderer
import kotlin.test.Test
import kotlin.test.assertEquals

class InlineOverlayRenderersTest {

    @Test
    fun `replaces every anchored and modal overlay renderer`() {
        assertEquals(
            setOf(
                LocalDialogRenderer,
                LocalBottomSheetRenderer,
                LocalDropdownRenderer,
                LocalTooltipRenderer,
                LocalShowcaseRenderer,
            ),
            InlineOverlayRenderers.values().map { it.compositionLocal }.toSet(),
        )
    }
}
