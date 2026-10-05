package com.huroofi.app.data.content

import kotlinx.serialization.json.Json

object ContentJson {
    /** Strict: unknown keys fail, so JSON and models cannot drift apart silently. */
    private val strict = Json { ignoreUnknownKeys = false }

    fun decode(text: String): ContentFile = strict.decodeFromString(ContentFile.serializer(), text)

    fun decodeStrokes(text: String): StrokeFile = strict.decodeFromString(StrokeFile.serializer(), text)
}
