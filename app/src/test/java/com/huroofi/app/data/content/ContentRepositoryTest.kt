package com.huroofi.app.data.content

import java.io.File
import java.io.FileNotFoundException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

private class FileAssetReader : AssetReader {
    override fun read(path: String): String = File("src/main/assets/$path").readText(Charsets.UTF_8)
}

class ContentRepositoryTest {
    private val repo = ContentRepository.load(FileAssetReader())

    @Test
    fun loadsAllLettersAndStagesFromJson() {
        assertEquals(28, repo.letters.size)
        assertEquals(7, repo.stages.size)
    }

    @Test
    fun letterLookupByIndex() {
        assertEquals("alif", repo.letter(1).nameLatin)
        assertEquals("yaa", repo.letter(28).nameLatin)
        assertThrows(NoSuchElementException::class.java) { repo.letter(29) }
    }

    @Test
    fun lettersInStageAreFourInAlphabetOrder() {
        assertEquals(listOf(1, 2, 3, 4), repo.lettersInStage(1).map { it.index })
        assertEquals(listOf(25, 26, 27, 28), repo.lettersInStage(7).map { it.index })
        assertEquals(emptyList<Letter>(), repo.lettersInStage(8))
    }

    @Test
    fun stageLookupAndColours() {
        assertEquals("Sunny Meadow", repo.stage(1).name)
        assertEquals(repo.stage(1).colors(), repo.stageColors(repo.letter(1)))
    }

    @Test
    fun resourceNamesDeriveFromJson() {
        val l = repo.letter(1)
        assertEquals("card_01_alif_lion", l.cardResourceName())
        assertEquals("pic_lion", l.pictureResourceName())
    }

    @Test
    fun strokesComeInWritingOrder() {
        val baa = repo.strokes(2)
        assertEquals(listOf(1, 2), baa.map { it.order })
        assertEquals(listOf(false, true), baa.map { it.dot })
        assertEquals(1, baa[1].points.size)
    }

    @Test
    fun strokesAreSortedByOrderWhateverTheFileOrder() {
        val loaded = ContentRepository.load(object : AssetReader {
            override fun read(path: String): String {
                val text = File("src/main/assets/$path").readText(Charsets.UTF_8)
                return if (path == ContentRepository.STROKES_PATH) """{"letters":[{"index":2,"strokes":[""" +
                    """{"order":2,"dot":true,"points":[[0.5,0.9]]},{"order":1,"dot":false,"points":[[0.9,0.1],[0.1,0.2]]}]}]}""" else text
            }
        })
        assertEquals(listOf(1, 2), loaded.strokes(2).map { it.order })
    }

    @Test
    fun letterWithoutStrokesGivesEmptyList() {
        assertEquals(emptyList<TraceStroke>(), repo.strokes(29))
    }

    @Test
    fun missingAssetPropagatesReaderError() {
        val broken = object : AssetReader {
            override fun read(path: String): String = throw FileNotFoundException(path)
        }
        assertThrows(FileNotFoundException::class.java) { ContentRepository.load(broken) }
    }
}
