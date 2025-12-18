package com.example.errenteriaapp.viewmodel



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.Character
import com.example.errenteriaapp.classes.DragGameUiState
import com.example.errenteriaapp.classes.GameWords
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DragGameViewModel : ViewModel() {

    // Estado inicial
    private val _uiState = MutableStateFlow(DragGameUiState())
    val uiState: StateFlow<DragGameUiState> = _uiState.asStateFlow()

    // Drop zones
    private val xantiDropZones = mutableListOf<Rect?>()
    private val maialenDropZones = mutableListOf<Rect?>()

    init {
        // Inicializar listas del tamaño correcto
        repeat(GameWords.XANTI_WORDS.size) { xantiDropZones.add(null) }
        repeat(GameWords.MAIALEN_WORDS.size) { maialenDropZones.add(null) }

        // Estado inicial
        resetGame()
    }

    fun updateXantiDropZone(index: Int, rect: Rect) {
        if (index < xantiDropZones.size) {
            xantiDropZones[index] = rect
        }
    }

    fun updateMaialenDropZone(index: Int, rect: Rect) {
        if (index < maialenDropZones.size) {
            maialenDropZones[index] = rect
        }
    }

    fun handleDrop(
        dropPoint: Offset,
        draggingWord: String?,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?,
        isDraggingFromSlot: Boolean
    ) {
        viewModelScope.launch {
            val word = draggingWord ?: return@launch

            // Buscar en zonas de Xanti
            xantiDropZones.forEachIndexed { index, rect ->
                if (rect?.contains(dropPoint) == true) {
                    updateAssignments(
                        targetCharacter = Character.XANTI,
                        targetIndex = index,
                        word = word,
                        draggingSlotCharacter = draggingSlotCharacter,
                        draggingSlotIndex = draggingSlotIndex,
                        isDraggingFromSlot = isDraggingFromSlot
                    )
                    return@launch
                }
            }

            // Buscar en zonas de Maialen
            maialenDropZones.forEachIndexed { index, rect ->
                if (rect?.contains(dropPoint) == true) {
                    updateAssignments(
                        targetCharacter = Character.MAIALEN,
                        targetIndex = index,
                        word = word,
                        draggingSlotCharacter = draggingSlotCharacter,
                        draggingSlotIndex = draggingSlotIndex,
                        isDraggingFromSlot = isDraggingFromSlot
                    )
                    return@launch
                }
            }
        }
    }

    private fun updateAssignments(
        targetCharacter: Character,
        targetIndex: Int,
        word: String,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?,
        isDraggingFromSlot: Boolean
    ) {
        _uiState.update { currentState ->
            when (targetCharacter) {
                Character.XANTI -> {
                    val currentAssignments = currentState.xantiAssignments.toMutableList()
                    val existingWord = currentAssignments[targetIndex]
                    currentAssignments[targetIndex] = word

                    var newState = currentState.copy(
                        xantiAssignments = currentAssignments
                    )

                    // Si veníamos de un slot, manejar intercambio
                    if (isDraggingFromSlot) {
                        newState = handleSlotExchange(
                            currentState = newState,
                            existingWord = existingWord,
                            draggingSlotCharacter = draggingSlotCharacter,
                            draggingSlotIndex = draggingSlotIndex
                        )
                    }
                    newState
                }
                Character.MAIALEN -> {
                    val currentAssignments = currentState.maialenAssignments.toMutableList()
                    val existingWord = currentAssignments[targetIndex]
                    currentAssignments[targetIndex] = word

                    var newState = currentState.copy(
                        maialenAssignments = currentAssignments
                    )

                    // Si veníamos de un slot, manejar intercambio
                    if (isDraggingFromSlot) {
                        newState = handleSlotExchange(
                            currentState = newState,
                            existingWord = existingWord,
                            draggingSlotCharacter = draggingSlotCharacter,
                            draggingSlotIndex = draggingSlotIndex
                        )
                    }
                    newState
                }
            }
        }
    }

    private fun handleSlotExchange(
        currentState: DragGameUiState,
        existingWord: String?,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?
    ): DragGameUiState {
        return when (draggingSlotCharacter) {
            Character.XANTI -> {
                draggingSlotIndex?.let { index ->
                    currentState.copy(
                        xantiAssignments = currentState.xantiAssignments.toMutableList().apply {
                            this[index] = existingWord
                        }
                    )
                } ?: currentState
            }
            Character.MAIALEN -> {
                draggingSlotIndex?.let { index ->
                    currentState.copy(
                        maialenAssignments = currentState.maialenAssignments.toMutableList().apply {
                            this[index] = existingWord
                        }
                    )
                } ?: currentState
            }
            null -> currentState
        }
    }

    fun checkAnswers(): Boolean {
        val currentState = _uiState.value

        val xantiAssignedWords = currentState.xantiAssignments
            .filterNotNull()
            .toSet()

        val maialenAssignedWords = currentState.maialenAssignments
            .filterNotNull()
            .toSet()

        return xantiAssignedWords == GameWords.XANTI_WORDS.toSet() &&
                maialenAssignedWords == GameWords.MAIALEN_WORDS.toSet()
    }

    fun resetGame() {
        viewModelScope.launch {
            _uiState.update {
                DragGameUiState(
                    allWords = GameWords.ALL_WORDS.shuffled(),
                    xantiAssignments = List(GameWords.XANTI_WORDS.size) { null },
                    maialenAssignments = List(GameWords.MAIALEN_WORDS.size) { null }
                )
            }
        }
    }

    fun showSuccessDialog(show: Boolean) {
        _uiState.update { it.copy(showSuccessDialog = show) }
    }

    fun showErrorDialog(show: Boolean) {
        _uiState.update { it.copy(showErrorDialog = show) }
    }
}