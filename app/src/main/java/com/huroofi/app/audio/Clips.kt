package com.huroofi.app.audio

import com.huroofi.app.data.content.Letter

/** An audio file, by path relative to the assets folder. */
@JvmInline
value class Clip(val path: String)

/**
 * Every clip name the app asks for. Letter and word paths come from letters.json; prompt and
 * sfx names follow the convention agreed in plan 03 (HANDOFF section 4 gives no file names).
 */
object Clips {
    const val PRAISE_COUNT = 3

    fun letter(l: Letter) = Clip(l.audio.letter)

    fun word(l: Letter) = Clip(l.audio.word)

    /** "Where is the duck?" style prompt, one per picture. */
    fun whereIs(l: Letter) = Clip("audio/prompts/where_is_${l.picture}.mp3")

    /** "Colour the baa" prompt for finger paint. */
    fun colour(l: Letter) = Clip("audio/prompts/colour_${l.nameLatin}.mp3")

    val whatShallWePlay = Clip("audio/prompts/what_shall_we_play.mp3")

    /** "وقت الراحة، إلى اللقاء غدًا!" on the Rest screen (plan 06 decision 6). */
    val timeToRest = Clip("audio/prompts/time_to_rest.mp3")

    /** "Which picture starts with the letter…?", followed by the letter clip (plan 07 decision 4). */
    val whichStartsWith = Clip("audio/prompts/which_starts_with.mp3")

    fun praise(n: Int): Clip {
        require(n in 1..PRAISE_COUNT) { "praise clip must be 1..$PRAISE_COUNT" }
        return Clip("audio/sfx/praise_$n.mp3")
    }

    /** Gentle sound for a wrong tap. Never harsh. */
    val boing = Clip("audio/sfx/boing.mp3")

    val cheer = Clip("audio/sfx/cheer.mp3")

    fun all(letters: List<Letter>): List<Clip> =
        letters.flatMap { listOf(letter(it), word(it), whereIs(it), colour(it)) } +
            whatShallWePlay + timeToRest + whichStartsWith + (1..PRAISE_COUNT).map(::praise) + boing + cheer
}
