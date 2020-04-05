package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.tree.*

class FunctionChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        val declaredFunctions = hashMapOf<String, HashSet<Int>>()
        for (function in file.functions) {
            if (function.name !in declaredFunctions.keys) {
                if (function.parameters.stream().distinct().count().toInt() != function.parameters.size) {
                    reportDuplicatedParameters(function)
                }
                val sizes = declaredFunctions.getOrDefault(function.name, hashSetOf())
                sizes.add(function.parameters.size)
                declaredFunctions[function.name] = sizes
            } else {
                val parameterSizes = declaredFunctions[function.name]!!
                if (function.parameters.size in parameterSizes) {
                    reportDuplicatedDeclaration(function)
                } else {
                    declaredFunctions[function.name]!!.add(function.parameters.size)
                }
            }
        }
        for (function in file.functions) {
            for (statement in function.body.statements) {
                inspectStatement(declaredFunctions, statement)
            }
        }
    }

    private fun inspectStatement(declaredFunctions: HashMap<String, HashSet<Int>>, statement: Statement) {
        when (statement) {
            is Assignment -> {
                inspectExpression(declaredFunctions, statement.rhs)
            }
            is IfStatement -> {
                val condition = statement.condition
                val thenBlock = statement.thenBlock
                val elseBlock = statement.elseBlock
                inspectExpression(declaredFunctions, condition)
                for (blockStatement in thenBlock.statements) {
                    inspectStatement(declaredFunctions, blockStatement)
                }
                if (elseBlock != null) {
                    for (blockStatement in elseBlock.statements) {
                        inspectStatement(declaredFunctions, blockStatement)
                    }
                }
            }
            is VariableDeclaration -> {
                val initializer = statement.initializer
                if (initializer != null) {
                    inspectExpression(declaredFunctions, initializer)
                }
            }
            is ReturnStatement -> {
                val result = statement.result
                if (result != null) {
                    inspectExpression(declaredFunctions, result)
                }
            }
            is Expression -> {
                inspectExpression(declaredFunctions, statement)
            }
        }
    }

    private fun inspectExpression(declaredFunctions: HashMap<String, HashSet<Int>>, expression: Expression) {
        if (expression is FunctionCall) {
            if (expression.function !in declaredFunctions.keys) {
                reportCallUnknown(expression)
            } else {
                if (expression.arguments.size !in declaredFunctions[expression.function]!!) {
                    reportWrongCall(expression)
                }
            }
            for (argument in expression.arguments) {
                inspectExpression(declaredFunctions, argument)
            }
        }
    }

    private fun reportCallUnknown(functionCall: FunctionCall) {
        reporter.report(functionCall, "Function '${functionCall.function}' is not declared")
    }

    private fun reportWrongCall(functionCall: FunctionCall) {
        val size = functionCall.arguments.size
        val parametersString = "parameter" + (if (size != 1) "s" else "")
        reporter.report(
            functionCall,
            "Function ${functionCall.function} with $size $parametersString is not declared"
        )
    }

    private fun reportDuplicatedDeclaration(declaration: FunctionDeclaration) {
        reporter.report(declaration, "Function ${declaration.name} is was already declared")
    }

    private fun reportDuplicatedParameters(declaration: FunctionDeclaration) {
        reporter.report(declaration, "Function ${declaration.name} has duplicated parameters")
    }
}