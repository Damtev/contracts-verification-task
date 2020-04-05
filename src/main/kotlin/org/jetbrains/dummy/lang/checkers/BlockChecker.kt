package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.ReportType
import org.jetbrains.dummy.lang.tree.*

class BlockChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {

    override fun inspect(file: File) {
        for (function in file.functions) {
            inspectBlock(function.body)
        }
    }

    private fun inspectBlock(block: Block) {
        if (block.statements.isEmpty()) {
            reportEmptyBlock(block)
        }
        block.statements.filterIsInstance<IfStatement>().forEach { inspectIfStatement(it) }
    }

    private fun inspectIfStatement(ifStatement: IfStatement) {
        inspectBlock(ifStatement.thenBlock)
        if (ifStatement.elseBlock != null) {
            inspectBlock(ifStatement.elseBlock)
        }
    }

    private fun reportEmptyBlock(block: Block) {
        reporter.report(
            block, "Block has empty body",
            ReportType.WARNING
        )
    }
}