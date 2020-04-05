package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.ReportType
import org.jetbrains.dummy.lang.tree.*

class IfChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {

    // it is pretty difficult to check function types
    override fun inspect(file: File) {
        for (function in file.functions) {
            inspectBlock(function.body)
        }
    }

    private fun inspectBlock(block: Block?) {
        block?.statements?.filterIsInstance<IfStatement>()?.forEach { inspectIfStatement(it) }
    }

    private fun inspectIfStatement(ifStatement: IfStatement) {
        if (ifStatement.condition is BooleanConst) {
            if (ifStatement.condition.value) {
                reportAlwaysTrue(ifStatement)
            } else {
                reportUnreachable(ifStatement)
            }
        }
        inspectBlock(ifStatement.thenBlock)
        inspectBlock(ifStatement.elseBlock)
    }

    private fun reportAlwaysTrue(ifStatement: IfStatement) {
        reporter.report(
            ifStatement, "Condition is always true",
            ReportType.WARNING
        )
    }

    private fun reportUnreachable(ifStatement: IfStatement) {
        reporter.report(
            ifStatement, "Code inside conditional block is unreachable",
            ReportType.WARNING
        )
    }
}