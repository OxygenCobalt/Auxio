package org.oxycblt.auxio.headunit.compat

data class HeadUnitCompatCandidate(
    val feature: HeadUnitCompatFeature,
    val sourceClass: String,
    val confidenceLabel: String,
    val portingDecisionLabel: String,
    val implementationStatus: String,
    val fallbackBehavior: String,
    val productionEligibility: String,
)

object HeadUnitCompatRegistry {
    val candidates: List<HeadUnitCompatCandidate> = listOf(
        HeadUnitCompatCandidate(HeadUnitCompatFeature.TWTHEME_WIDGET_METADATA, "TWTHEME/iLauncher references", "Inferred", "Requires TS18 runtime validation", "implemented_candidate", "NoSession widget fallback", "requires_validation"),
        HeadUnitCompatCandidate(HeadUnitCompatFeature.STOCK_TMUSIC_PARITY, "Stock t-music behavior mapping", "Inferred", "Directly reusable requirement", "implemented_candidate", "Route fallback to library home", "requires_validation"),
        HeadUnitCompatCandidate(HeadUnitCompatFeature.ZLINK_TLINK_COEXISTENCE, "ZLink/TLink coexistence references", "Hypothesis", "Reusable validation idea", "implemented_candidate", "No forced autoplay after focus loss", "requires_validation"),
        HeadUnitCompatCandidate(HeadUnitCompatFeature.HARDWARE_MEDIA_KEYS, "Android media key handling patterns", "Observed", "Directly reusable requirement", "implemented_candidate", "Drop unsafe key forwards", "eligible_tier1"),
        HeadUnitCompatCandidate(HeadUnitCompatFeature.ENTRYPOINT_DEEPLINK_STABILITY, "Launcher shortcut patterns", "Observed", "Directly reusable requirement", "implemented_candidate", "Main route fallback", "eligible_tier1"),
        HeadUnitCompatCandidate(HeadUnitCompatFeature.MEDIA_RICH_METADATA, "MediaSession metadata expectations", "Inferred", "Requires TS18 runtime validation", "implemented_candidate", "clear metadata when no session", "requires_validation"),
    )
}
