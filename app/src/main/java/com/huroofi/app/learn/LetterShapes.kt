package com.huroofi.app.learn

/** The four written forms of a letter (glossary: Letter shape). */
enum class ShapeKind(val label: String) { ALONE("Alone"), START("Start"), MIDDLE("Middle"), END("End") }

/** Letters that never join the letter after them (glossary: Non-joining letter). */
val NON_JOINING = setOf("أ", "د", "ذ", "ر", "ز", "و")

private const val TATWEEL = "ـ"

/**
 * Shapes built from the letter and a tatweel, so the font does the joining (plan 07 decision 2).
 * Non-joining letters have only alone and end.
 */
fun letterShapes(letter: String): List<Pair<ShapeKind, String>> {
    val all = listOf(
        ShapeKind.ALONE to letter,
        ShapeKind.START to letter + TATWEEL,
        ShapeKind.MIDDLE to TATWEEL + letter + TATWEEL,
        ShapeKind.END to TATWEEL + letter,
    )
    return if (letter in NON_JOINING) all.filter { it.first == ShapeKind.ALONE || it.first == ShapeKind.END } else all
}
