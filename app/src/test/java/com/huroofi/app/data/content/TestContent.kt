package com.huroofi.app.data.content

import java.io.File

/** The real letters.json, read from the app assets, for tests that need all 28 letters. */
object TestContent {
    val repo: ContentRepository by lazy {
        ContentRepository.load(object : AssetReader {
            override fun read(path: String): String = File("src/main/assets/$path").readText(Charsets.UTF_8)
        })
    }
}
