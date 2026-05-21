package com.example.pan.util

object ClassroomMatcher {

    // Α-series: Greek Α (U+0391) or Latin A (U+0041) followed by 1–3 digits
    private val alphaSeriesRegex = Regex("^[AΑ]\\d{1,3}$")

    // Δ-series: Greek Δ (U+0394) followed by 1–3 digits
    private val deltaSeriesRegex = Regex("^Δ\\d{1,3}$")

    // Canonical spellings for fixed-name rooms (used for display and comparison)
    private val fixedRooms = listOf(
        "CSLab",
        "Αμφιθέατρο Α",
        "Αμφιθέατρο Β",
        "Αμφιθέατρο Γ",
        "Αμφιθέατρο Κιντής",
        "Αίθουσα Χ"
    )
    private val fixedRoomsLower = fixedRooms.map { it.lowercase() }

    /**
     * Attempts to match a raw OCR string to a known classroom identifier.
     * Returns the canonical room name, or null if no match.
     * Latin A (U+0041) is normalised to Greek Α (U+0391) before matching
     * so that "A21" and "Α21" both resolve to "Α21".
     */
    fun normalize(raw: String): String? {
        val trimmed   = raw.trim()
        val normalized = trimmed.replace('A', 'Α')   // U+0041 → U+0391

        if (alphaSeriesRegex.matches(normalized)) return normalized
        if (deltaSeriesRegex.matches(trimmed))    return trimmed

        val lower = trimmed.lowercase()
        val idx   = fixedRoomsLower.indexOf(lower)
        if (idx >= 0) return fixedRooms[idx]

        return null
    }
}