package com.example.errenteriaapp.classes




data class DragGameUiState(
    val allWords: List<String> = emptyList(),
    val xantiAssignments: List<String?> = emptyList(),
    val maialenAssignments: List<String?> = emptyList(),
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    // Propiedades computadas
    val allSlotsFilled: Boolean
        get() = xantiAssignments.all { it != null } &&
                maialenAssignments.all { it != null }

    val availableWords: List<String>
        get() = allWords.filter { word ->
            !xantiAssignments.contains(word) && !maialenAssignments.contains(word)
        }
}