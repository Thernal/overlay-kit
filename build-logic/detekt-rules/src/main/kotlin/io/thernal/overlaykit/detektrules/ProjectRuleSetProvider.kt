package io.thernal.overlaykit.detektrules

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import io.thernal.overlaykit.detektrules.collections.UnsafeCollectionIndexAccess
import io.thernal.overlaykit.detektrules.packageboundary.LayerPackageBoundary
import io.thernal.overlaykit.detektrules.packageboundary.LayerPackageRequired
import io.thernal.overlaykit.detektrules.preview.PreviewMustBePrivate
import io.thernal.overlaykit.detektrules.style.ExpressionBodyNotAllowed
import io.thernal.overlaykit.detektrules.style.MultilineConstructorRequired

class ProjectRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSetId("project")

    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            ::PreviewMustBePrivate,
            ::UnsafeCollectionIndexAccess,
            ::LayerPackageBoundary,
            ::LayerPackageRequired,
            ::ExpressionBodyNotAllowed,
            ::MultilineConstructorRequired,
        ),
    )
}
