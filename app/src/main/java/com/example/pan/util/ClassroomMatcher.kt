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
     * Maps common Latin homoglyphs to Greek characters to support "reading" 
     * Greek signs using a Latin-based OCR.
     */
    fun normalize(raw: String): String? {
        val trimmed = raw.trim().uppercase()
        
        // Map Latin homoglyphs to Greek for better matching of Greek signs
        val greekified = trimmed
            .replace('A', 'Α')
            .replace('B', 'Β')
            .replace('E', 'Ε')
            .replace('Z', 'Ζ')
            .replace('H', 'Η')
            .replace('I', 'Ι')
            .replace('K', 'Κ')
            .replace('M', 'Μ')
            .replace('N', 'Ν')
            .replace('O', 'Ο')
            .replace('P', 'Ρ')
            .replace('T', 'Τ')
            .replace('Y', 'Υ')
            .replace('X', 'Χ')

        if (alphaSeriesRegex.matches(greekified)) return greekified
        if (deltaSeriesRegex.matches(greekified)) return greekified

        // Check for fixed rooms (case insensitive)
        val lower = trimmed.lowercase()
        val idx = fixedRoomsLower.indexOfFirst { it == lower || normalizeGreek(it) == normalizeGreek(lower) }
        if (idx >= 0) return fixedRooms[idx]

        return null
    }

    private fun normalizeGreek(text: String): String {
        return text.lowercase()
            .replace('ά', 'α')
            .replace('έ', 'ε')
            .replace('ή', 'η')
            .replace('ί', 'ι')
            .replace('ό', 'ο')
            .replace('ύ', 'υ')
            .replace('ώ', 'ω')
            .replace('ϊ', 'ι')
            .replace('ϋ', 'υ')
    }
}
