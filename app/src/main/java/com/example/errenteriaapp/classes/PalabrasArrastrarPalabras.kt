package com.example.errenteriaapp.classes

object GameWords {
    val XANTI_WORDS = listOf(
        "Alkondara Zuria",
        "Txaleko Gorria",
        "Txapela",
        "Gona Urdina"
    )

    val MAIALEN_WORDS = listOf(
        "Blusa Zuria",
        "Kortse Beltza",
        "Zapia Buruan Lotuta",
        "Zapia Lepoan Lotuta",
        "Mantal Beltza"
    )

    val EXTRA_WORDS = listOf("Gona Gorria")

    val ALL_WORDS = (XANTI_WORDS + MAIALEN_WORDS + EXTRA_WORDS).shuffled()
}