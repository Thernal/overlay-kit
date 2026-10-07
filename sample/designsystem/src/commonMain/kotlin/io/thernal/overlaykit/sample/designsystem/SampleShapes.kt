package io.thernal.overlaykit.sample.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Corner radii, from a tooltip's to a sheet's. */
@Immutable
data class SampleShapes(
    val small: Dp = 6.dp,
    val medium: Dp = 8.dp,
    val large: Dp = 12.dp,
)
