package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.tree.Block
import org.jetbrains.dummy.lang.tree.File
import org.jetbrains.dummy.lang.tree.IfStatement
import org.jetbrains.dummy.lang.tree.ReturnStatement

class ReturnChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {

    private fun checkMultipleReturn(block: Block) {
        if (block.statements.filterIsInstance<ReturnStatement>().size > 1) {
            reportMultipleReturn(block)
        }
    }

    private fun checkIfStatements(block: Block) {
        block.statements.filterIsInstance<IfStatement>().forEach { inspectIfStatement(it) }
    }

    override fun inspect(file: File) {
        for (function in file.functions) {
            checkMultipleReturn(function.body)
            checkIfStatements(function.body)
        }
    }

    private fun inspectIfStatement(ifStatement: IfStatement) {
        checkMultipleReturn(ifStatement.thenBlock)
        checkIfStatements(ifStatement.thenBlock)
        if (ifStatement.elseBlock != null) {
            checkMultipleReturn(ifStatement.elseBlock)
            checkIfStatements(ifStatement.elseBlock)
        }
    }

    private fun reportMultipleReturn(block: Block) {
        reporter.report(block, "Block has multiple returns")
    }
}