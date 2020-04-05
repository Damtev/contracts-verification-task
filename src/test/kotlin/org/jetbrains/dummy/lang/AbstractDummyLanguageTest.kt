package org.jetbrains.dummy.lang

import org.junit.Assert
import java.io.ByteArrayOutputStream
import java.io.File

abstract class AbstractDummyLanguageTest {
    fun doTest(path: String) {
        val expectedFile = File(path.replace(".dummy", ".txt"))
        if (!expectedFile.exists()) {
            expectedFile.createNewFile()
        }
        val expected = expectedFile.readLines().filter { s -> s.isNotEmpty() }.toHashSet()

        val outputStream = ByteArrayOutputStream()
        val analyzer = DummyLanguageAnalyzer(outputStream)
        analyzer.analyze(path)
        val actual = outputStream.toString().split(System.lineSeparator()).filter { s -> s.isNotEmpty() }.toHashSet()
        actual.forEach { println(it) }
        Assert.assertEquals(expected, actual)
//        expectedFile.printWriter().use { writer ->
//            actual.forEach { writer.println(it) }
//        }
    }
}