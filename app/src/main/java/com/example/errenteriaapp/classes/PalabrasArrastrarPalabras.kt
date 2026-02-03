package com.example.errenteriaapp.classes

import com.example.errenteriaapp.R

/**
 * Jokoaren hitzen konstanteak eta funtzioak gordetzen ditu.
 * Hitz-izen lokalizatuak ez konparatzeko gako estatuak erabiltzen ditu.
 */
object GameWords {
    // Xanti pertsonaiko jantzien gakoak
    const val XANTI_WHITE_SHIRT = "xanti_white_shirt"
    const val XANTI_RED_VEST = "xanti_red_vest"
    const val XANTI_BERET = "xanti_beret"
    const val XANTI_BLUE_SKIRT = "xanti_blue_skirt"

    // Maialen pertsonaiko jantzien gakoak
    const val MAIALEN_WHITE_BLOUSE = "maialen_white_blouse"
    const val MAIALEN_BLACK_CORSET = "maialen_black_corset"
    const val MAIALEN_HEAD_SCARF = "maialen_head_scarf"
    const val MAIALEN_NECK_SCARF = "maialen_neck_scarf"
    const val MAIALEN_BLACK_APRON = "maialen_black_apron"

    // Jantzirik gabeko hitzaren gakoa
    const val EXTRA_RED_SKIRT = "extra_red_skirt"

    /** Xanti pertsonaiko hitz guztiak */
    val XANTI_WORDS = listOf(
        XANTI_WHITE_SHIRT,
        XANTI_RED_VEST,
        XANTI_BERET,
        XANTI_BLUE_SKIRT
    )

    /** Maialen pertsonaiko hitz guztiak */
    val MAIALEN_WORDS = listOf(
        MAIALEN_WHITE_BLOUSE,
        MAIALEN_BLACK_CORSET,
        MAIALEN_HEAD_SCARF,
        MAIALEN_NECK_SCARF,
        MAIALEN_BLACK_APRON
    )

    /** Jantzirik gabeko hitzak */
    val EXTRA_WORDS = listOf(EXTRA_RED_SKIRT)

    /** Hitz guztiak nahastuta */
    val ALL_WORDS = (XANTI_WORDS + MAIALEN_WORDS + EXTRA_WORDS).shuffled()

    /**
     * Gako batetik bere etiketaren baliabide-identifikadoreara mapeatzen du.
     * Gakoa -> strings.xml-en dagoen baliabide-identifikadorea
     */
    private val WORD_LABELS = mapOf(
        XANTI_WHITE_SHIRT to R.string.taula_word_xanti_white_shirt,
        XANTI_RED_VEST to R.string.taula_word_xanti_red_vest,
        XANTI_BERET to R.string.taula_word_xanti_beret,
        XANTI_BLUE_SKIRT to R.string.taula_word_xanti_blue_skirt,
        MAIALEN_WHITE_BLOUSE to R.string.taula_word_maialen_white_blouse,
        MAIALEN_BLACK_CORSET to R.string.taula_word_maialen_black_corset,
        MAIALEN_HEAD_SCARF to R.string.taula_word_maialen_head_scarf,
        MAIALEN_NECK_SCARF to R.string.taula_word_maialen_neck_scarf,
        MAIALEN_BLACK_APRON to R.string.taula_word_maialen_black_apron,
        EXTRA_RED_SKIRT to R.string.taula_word_extra_red_skirt
    )

    /**
     * Hitz-gako baten etiketerako baliabide-identifikadorea itzultzen du.
     * @param wordKey Hitzaren gakoa
     * @return Etiketaren baliabide-identifikadorea edo "ezezaguna" mezurako baliabidea
     */
    fun labelRes(wordKey: String): Int {
        return WORD_LABELS[wordKey] ?: R.string.taula_word_unknown
    }
}