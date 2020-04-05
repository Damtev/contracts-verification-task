package org.jetbrains.dummy.lang

import org.jetbrains.dummy.lang.tree.Element
import java.io.OutputStream
import java.io.PrintStream

enum class ReportType(val type : String) {
    ERROR("ERROR"),
    WARNING("Warning")
}

class DiagnosticReporter(
    outputStream: OutputStream
) {
    private val outputStream = PrintStream(outputStream)

    fun report(element: Element, message: String, reportType: ReportType = ReportType.ERROR) {
        outputStream.println("${reportType.type}: line ${element.line}: $message")
    }
}