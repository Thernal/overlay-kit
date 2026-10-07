package io.thernal.overlaykit.sample.designsystem.overlay

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownStyle
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseStyle
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarTones
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayStyles
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle
import io.thernal.overlaykit.sample.designsystem.SampleTheme

/**
 * The design system's tokens mapped onto every overlay's style — the one file an app writes in its own
 * design system, so overlays look like the rest of the app and change with it. The root installs it:
 *
 * ```
 * SampleTheme {
 *     OverlayTheme(styles = sampleOverlayStyles()) {
 *         OverlayHost { App() }
 *     }
 * }
 * ```
 *
 * Only looks are mapped; behaviour (thresholds, auto-dismiss, insets) keeps the kit's defaults.
 */
@Composable
@ReadOnlyComposable
fun sampleOverlayStyles(): OverlayStyles {
    val colors = SampleTheme.colors
    val shapes = SampleTheme.shapes
    val spacing = SampleTheme.spacing
    val typography = SampleTheme.typography
    val motion = SampleTheme.motion
    val balloon = TooltipStyle(
        containerColor = colors.accent,
        borderColor = colors.line,
        cornerRadius = shapes.small,
        contentPadding = PaddingValues(horizontal = spacing.medium, vertical = spacing.small),
    )
    return OverlayStyles(
        dialog = DialogStyle(
            containerColor = colors.paper,
            borderColor = colors.line,
            shape = RoundedCornerShape(shapes.large),
            contentPadding = PaddingValues(spacing.large),
            scrimColor = colors.scrim,
            animationMillis = motion.fastMillis,
        ),
        bottomSheet = BottomSheetStyle(
            containerColor = colors.paper,
            shape = RoundedCornerShape(topStart = shapes.large, topEnd = shapes.large),
            contentPadding = PaddingValues(spacing.large),
            scrimColor = colors.scrim,
            animationMillis = motion.mediumMillis,
            dragHandleColor = colors.line,
        ),
        dropdown = DropdownStyle(
            containerColor = colors.paper,
            borderColor = colors.line,
            shape = RoundedCornerShape(shapes.medium),
            animationMillis = motion.fastMillis,
        ),
        tooltip = balloon,
        snackbar = SnackbarStyle(
            containerColor = colors.ink,
            contentColor = colors.text,
            borderColor = colors.line,
            shape = RoundedCornerShape(shapes.medium),
            outerPadding = PaddingValues(spacing.large),
            contentPadding = PaddingValues(horizontal = spacing.large, vertical = spacing.medium),
            itemSpacing = spacing.medium,
            textStyle = typography.body,
            actionTextStyle = typography.label,
            tones = SnackbarTones(neutral = colors.accent),
        ),
        showcase = ShowcaseStyle(scrimColor = colors.scrim, balloon = balloon),
    )
}
