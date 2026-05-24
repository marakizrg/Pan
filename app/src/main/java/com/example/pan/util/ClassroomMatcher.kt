package com.example.pan.util

object ClassroomMatcher {

    // Α-series: Greek Α (U+0391) or Latin A (U+0041) followed by 1–3 digits
    private val alphaSeriesRegex = Regex("^[AΑ]\\d{1,3}$")
    // Δ-series: Greek Δ (U+0394) followed by 1–3 digits
    private val deltaSeriesRegex = Regex("^Δ\\d{1,3}$")

    /**
     * Attempts to match a raw OCR string to a known classroom identifier.
     * Maps common Latin homoglyphs to Greek and handles "Αμφιθέατρο" fuzzy matching
     * to compensate for Latin-only OCR limitations on Greek signs.
     */
    fun normalize(raw: String): String? {
        val trimmed = raw.trim().uppercase()
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")

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

        // Fuzzy match for "Αμφιθέατρο" variations
        // We look for parts of the word since Φ/Θ are often missed by Latin OCR
        if (greekified.contains(Regex("ΑΜ.*ΑΤΡΟ")) || greekified.contains("ΑΜΦ") || greekified.contains("ΕΑΤΡΟ")) {
            // Check for suffix A/B/C/G or common digit misreads (4 for A, 8 for B)
            if (greekified.endsWith("Α") || greekified.contains("Α ") || greekified.contains(" 4") || greekified.endsWith("4")) return "Αμφιθέατρο Α"
            if (greekified.endsWith("Β") || greekified.contains("Β ") || greekified.contains(" 8") || greekified.endsWith("8") || greekified.contains(" B")) return "Αμφιθέατρο Β"
            if (greekified.endsWith("Γ") || greekified.contains("Γ ") || greekified.endsWith("G") || greekified.contains(" G")) return "Αμφιθέατρο Γ"
            if (greekified.contains("ΚΙΝΤΗΣ") || greekified.contains("KINTH")) return "Αμφιθέατρο Κιντής"
        }

        if (alphaSeriesRegex.matches(greekified)) return greekified
        if (deltaSeriesRegex.matches(greekified)) return greekified
        
        // Handle explicit "ΑΜΦΙΘΕΑΤΡΟ X" if it was read perfectly
        if (greekified.startsWith("ΑΜΦΙΘΕΑΤΡΟ")) {
            val suffix = greekified.removePrefix("ΑΜΦΙΘΕΑΤΡΟ").trim()
            return when (suffix) {
                "Α", "4" -> "Αμφιθέατρο Α"
                "Β", "8" -> "Αμφιθέατρο Β"
                "Γ", "G" -> "Αμφιθέατρο Γ"
                "ΚΙΝΤΗΣ" -> "Αμφιθέατρο Κιντής"
                else -> "Αμφιθέατρο $suffix"
            }
        }

        // Fixed room cases
        if (greekified == "CSLAB" || greekified == "CSLAB1") return "CSLab"
        if (greekified == "X" || greekified == "ΑΙΘΟΥΣΑ Χ" || greekified == "ΑΙΘΟΥΣΑ X") return "Αίθουσα Χ"

        return null
    }

    /**
     * Normalizes room names for comparison between different sources (OCR vs Schedule).
     * e.g. "Αμφιθέατρο Α" -> "ΑΜΦΑ", "Αμφ. Β" -> "ΑΜΦΒ", "Α24" -> "Α24"
     */
    fun getCanonicalId(name: String): String {
        return name.uppercase()
            .replace("ΑΜΦΙΘΕΑΤΡΟ", "ΑΜΦ")
            .replace("ΑΜΦ.", "ΑΜΦ")
            .replace("ΑΙΘΟΥΣΑ", "")
            .replace(Regex("[.\\s]+"), "") // Remove dots and spaces
            .replace('A', 'Α')
            .replace('B', 'Β')
            .replace('E', 'Ε')
            .replace('H', 'Η')
            .replace('I', 'Ι')
            .replace('K', 'Κ')
            .replace('M', 'Μ')
            .replace('N', 'Ν')
            .replace('O', 'Ο')
            .replace('P', 'Ρ')
            .replace('T', 'Τ')
            .replace('X', 'Χ')
            .trim()
    }
}
