package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.ReportType
import org.jetbrains.dummy.lang.tree.Expression
import org.jetbrains.dummy.lang.tree.File
import org.jetbrains.dummy.lang.tree.FunctionCall

class ExpressionChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {

    // it is pretty difficult to check unused return value because of possible recursion
    override fun inspect(file: File) {
        for (function in file.functions) {
            function.body.statements.filterIsInstance<Expression>().filter { it !is FunctionCall }
                .forEach { reportUnusedExpression(it) }
        }
    }

    private fun reportUnusedExpression(expression: Expression) {
        reporter.report(expression, "Expression is unused",
            ReportType.WARNING
        )
    }
}