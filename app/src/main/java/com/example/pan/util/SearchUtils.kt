package com.example.pan.util

import java.text.Normalizer
import java.util.Locale

object SearchUtils {

    /**
     * Normalizes a string for fuzzy matching (Greek and Latin).
     * 1. Decomposes characters (Normalizer.Form.NFD).
     * 2. Removes accents/diacritics.
     * 3. Converts to lowercase.
     * 4. Maps Greek characters to Latin equivalents (Greeklish).
     */
    fun normalizeForSearch(text: String): String {
        // Step 1 & 2: Remove accents (this handles both Latin and some Greek cases)
        val temp = Normalizer.normalize(text, Normalizer.Form.NFD)
        val accentRemoved = temp.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        
        val lower = accentRemoved.lowercase(Locale.ROOT)
        val sb = StringBuilder()
        
        var i = 0
        while (i < lower.length) {
            val char = lower[i]
            val nextChar = if (i + 1 < lower.length) lower[i + 1] else null
            
            // Map Greek characters to Latin (Greeklish)
            val mapping = when {
                char == 'θ' -> "th"
                char == 'χ' -> "ch"
                char == 'ψ' -> "ps"
                char == 'α' && nextChar == 'υ' -> { i++; "av" }
                char == 'ε' && nextChar == 'υ' -> { i++; "ev" }
                char == 'ο' && nextChar == 'υ' -> { i++; "ou" }
                char == 'μ' && nextChar == 'π' -> { i++; "b" }
                char == 'ν' && nextChar == 'τ' -> { i++; "d" }
                char == 'γ' && nextChar == 'κ' -> { i++; "g" }
                char == 'γ' && nextChar == 'γ' -> { i++; "g" }
                else -> null
            }
            
            if (mapping != null) {
                sb.append(mapping)
            } else {
                val singleMapping = when (char) {
                    'α' -> 'a'
                    'β' -> 'v'
                    'γ' -> 'g'
                    'δ' -> 'd'
                    'ε' -> 'e'
                    'ζ' -> 'z'
                    'η' -> 'i'
                    'ι' -> 'i'
                    'κ' -> 'k'
                    'λ' -> 'l'
                    'μ' -> 'm'
                    'ν' -> 'n'
                    'ξ' -> 'x'
                    'ο' -> 'o'
                    'π' -> 'p'
                    'ρ' -> 'r'
                    'σ', 'ς' -> 's'
                    'τ' -> 't'
                    'υ' -> 'y'
                    'φ' -> 'f'
                    'ω' -> 'o'
                    else -> char
                }
                sb.append(singleMapping)
            }
            i++
        }
        return sb.toString()
    }
}
