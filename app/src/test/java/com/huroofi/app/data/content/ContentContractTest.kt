package com.huroofi.app.data.content

import com.huroofi.app.toddler.isRed
import com.huroofi.app.ui.theme.StageColors
import java.io.File
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract between data/letters.json and the app. Reads the real file the app ships.
 * Gradle runs unit tests with the module directory (app/) as working directory.
 */
class ContentContractTest {
    private val assetsJson = File("src/main/assets/letters.json")
    private val packJson = File("../data/letters.json")
    private val drawables = File("src/main/res/drawable-nodpi")
    private val assetsStrokes = File("src/main/assets/strokes.json")
    private val packStrokes = File("../data/strokes.json")

    private val content: ContentFile by lazy { ContentJson.decode(assetsJson.readText(Charsets.UTF_8)) }
    private val strokes: StrokeFile by lazy { ContentJson.decodeStrokes(assetsStrokes.readText(Charsets.UTF_8)) }

    @Test
    fun shippedJsonIsByteIdenticalToThePack() {
        assertTrue(assetsJson.exists() && packJson.exists())
        assertEquals(
            packJson.readBytes().toList().filterNot { it == '\r'.code.toByte() },
            assetsJson.readBytes().toList().filterNot { it == '\r'.code.toByte() },
        )
    }

    @Test
    fun strictDecodeSucceedsAndShapeIs28LettersIn7StagesOf4() {
        assertEquals(28, content.letters.size)
        assertEquals((1..28).toList(), content.letters.map { it.index })
        assertEquals((1..7).toList(), content.stages.map { it.stage })
        content.stages.forEach { assertEquals(4, it.letters.size) }
    }

    @Test
    fun everyLetterBelongsToTheStageThatListsIt() {
        content.letters.forEach { l ->
            val stage = content.stages.single { it.stage == l.stage }
            assertTrue("${l.letter} not in stage ${l.stage}", l.letter in stage.letters)
        }
        assertEquals(
            content.letters.map { it.letter },
            content.stages.flatMap { it.letters },
        )
    }

    @Test
    fun stageColoursAreValidHex() {
        content.stages.forEach { s ->
            val c = StageColors.fromHex(s.border, s.pastel, s.accentDark)
            assertEquals(s.colors(), c)
        }
    }

    /** Stage colours paint child screens, so they follow the no-red rule too (plan 05 decision 2). */
    @Test
    fun noStageColourIsRed() {
        content.stages.forEach { s ->
            val c = s.colors()
            listOf("border" to c.border, "pastel" to c.pastel, "accent_dark" to c.accent).forEach { (name, colour) ->
                assertFalse("stage ${s.stage} $name is red", isRed(colour))
            }
        }
    }

    @Test
    fun wordSplitIsConsistent() {
        content.letters.forEach { l ->
            assertEquals(l.wordAr, l.wordFirst + l.wordRest)
        }
    }

    @Test
    fun everyCardAndPictureHasAnAppResource() {
        content.letters.forEach { l ->
            assertTrue("missing ${l.cardResourceName()}", File(drawables, l.cardResourceName() + ".png").exists())
            assertTrue("missing ${l.pictureResourceName()}", File(drawables, l.pictureResourceName() + ".png").exists())
        }
    }

    @Test
    fun bothFontsHaveAnAppResource() {
        assertTrue(File("src/main/res/font/baloo_bhaijaan_2.ttf").exists())
        assertTrue(File("src/main/res/font/noto_naskh_arabic.ttf").exists())
    }

    @Test
    fun shippedStrokesAreByteIdenticalToData() {
        assertTrue(assetsStrokes.exists() && packStrokes.exists())
        assertEquals(
            packStrokes.readBytes().toList().filterNot { it == '\r'.code.toByte() },
            assetsStrokes.readBytes().toList().filterNot { it == '\r'.code.toByte() },
        )
    }

    /** Plan 09 task 1.4 tightens this to exactly 1..28. */
    @Test
    fun strokeLettersExistOnceEach() {
        val indices = strokes.letters.map { it.index }
        assertEquals(indices.distinct(), indices)
        assertTrue("unknown index in $indices", indices.all { i -> content.letters.any { it.index == i } })
    }

    @Test
    fun strokeOrderIsOneToNWithoutGaps() {
        strokes.letters.forEach { l ->
            assertEquals("letter ${l.index}", (1..l.strokes.size).toList(), l.strokes.map { it.order }.sorted())
        }
    }

    @Test
    fun strokePointsArePairsInsideTheInkBox() {
        strokes.letters.forEach { l ->
            l.strokes.forEach { s ->
                s.points.forEach { p ->
                    assertEquals("letter ${l.index} stroke ${s.order}", 2, p.size)
                    assertTrue("letter ${l.index} stroke ${s.order} point $p", p.all { it in 0f..1f })
                }
            }
        }
    }

    @Test
    fun aDotIsOnePointAndAnyOtherStrokeAtLeastTwo() {
        strokes.letters.forEach { l ->
            l.strokes.forEach { s ->
                if (s.dot) {
                    assertEquals("letter ${l.index} stroke ${s.order}", 1, s.points.size)
                } else {
                    assertTrue("letter ${l.index} stroke ${s.order}", s.points.size >= 2)
                }
            }
            assertTrue("letter ${l.index} has no body stroke", l.strokes.any { !it.dot })
        }
    }

    @Test
    fun unknownStrokeKeysFailTheDecoder() {
        val withExtra = assetsStrokes.readText(Charsets.UTF_8).replaceFirst("\"order\": 1,", "\"order\": 1, \"surprise\": true,")
        assertThrows(SerializationException::class.java) { ContentJson.decodeStrokes(withExtra) }
    }

    @Test
    fun unknownJsonKeysFailTheDecoder() {
        val withExtra = assetsJson.readText(Charsets.UTF_8).replaceFirst("\"index\": 1,", "\"index\": 1, \"surprise\": true,")
        assertThrows(SerializationException::class.java) { ContentJson.decode(withExtra) }
    }

    @Test
    fun missingJsonKeysFailTheDecoder() {
        val withoutName = assetsJson.readText(Charsets.UTF_8).replaceFirst("\"name_latin\": \"alif\",", "")
        assertThrows(SerializationException::class.java) { ContentJson.decode(withoutName) }
    }
}
