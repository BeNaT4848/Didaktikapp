package com.example.errenteriaapp.classes

/**
 * Arrastratzeko jokoaren egoera interfazeari erakusteko.
 *
 * @property allWords Erabil daitezkeen hitz guztien zerrenda
 * @property xantiAssignments Xanti pertsonaiari esleitutako hitzen zerrenda (null = hutsik)
 * @property maialenAssignments Maialen pertsonaiari esleitutako hitzen zerrenda (null = hutsik)
 * @property showSuccessDialog Arrakasta mezua erakutsi behar den
 * @property showErrorDialog Errore mezua erakutsi behar den
 * @property puntuacion Jokalariaren puntuazioa
 */
data class DragGameUiState(
    val allWords: List<String> = emptyList(),
    val xantiAssignments: List<String?> = emptyList(),
    val maialenAssignments: List<String?> = emptyList(),
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val puntuacion: Int = 0
) {
    /**
     * Jokoan hutsik dauden lekurik geratzen diren.
     * @return true pertsonaia bakoitzari leku guztiak beteta badaude
     */
    val allSlotsFilled: Boolean
        get() = xantiAssignments.all { it != null } &&
                maialenAssignments.all { it != null }

    /**
     * Oraindik erabili gabe dauden hitzak.
     * @return Erabili daitezkeen hitzen zerrenda
     */
    val availableWords: List<String>
        get() {
            val palabrasUsadas = (xantiAssignments.filterNotNull() + maialenAssignments.filterNotNull()).toSet()
            return allWords.filter { it !in palabrasUsadas }
        }
}