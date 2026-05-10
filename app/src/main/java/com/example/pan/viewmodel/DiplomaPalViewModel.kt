package com.example.pan.viewmodel

import androidx.lifecycle.ViewModel

data class DegreeProgress(
    val requiredEcts: Int     = 240,
    val acquiredEcts: Int     = 188,
    val missingMandatory: Int = 4,
    val missingElective: Int  = 2
) {
    val progressFraction: Float get() = acquiredEcts.toFloat() / requiredEcts
    val progressPercent: Int    get() = (progressFraction * 100).toInt()
    val remainingEcts: Int      get() = requiredEcts - acquiredEcts
}

class DiplomaPalViewModel : ViewModel() {
    val progress = DegreeProgress()
}
