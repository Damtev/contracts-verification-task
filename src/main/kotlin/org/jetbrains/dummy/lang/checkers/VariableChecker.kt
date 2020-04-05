package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.tree.*

class VariableChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        for (function in file.functions) {
            val declaredVariables = hashSetOf<String>()
            val initializedVariables = hashSetOf<String>()
            declaredVariables.addAll(function.parameters)
            initializedVariables.addAll(function.parameters)
            for (statement in function.body.statements) {
                inspectStatement(declaredVariables, initializedVariables, statement)
            }
        }
    }

    private fun inspectStatement(
        declaredVariables: HashSet<String>,
        initializedVariables: HashSet<String>,
        statement: Statement
    ) {
        when (statement) {
            is Assignment -> {
                // А как так получилось, что Assignment не является VariableAccess?
                val variable = statement.variable
                if (variable !in declaredVariables) {
                    reportAccessBeforeDeclaration(VariableAccess(statement.line, variable))
                }
                initializedVariables.add(variable)
                inspectExpression(declaredVariables, initializedVariables, statement.rhs)
            }
            is IfStatement -> {
                val condition = statement.condition
                val thenBlock = statement.thenBlock
                val elseBlock = statement.elseBlock
                inspectExpression(declaredVariables, initializedVariables, condition)
                for (blockStatement in thenBlock.statements) {
                    inspectStatement(declaredVariables, initializedVariables, blockStatement)
                }
                if (elseBlock != null) {
                    for (blockStatement in elseBlock.statements) {
                        inspectStatement(declaredVariables, initializedVariables, blockStatement)
                    }
                }
            }
            is VariableDeclaration -> {
                val name = statement.name
                if (name !in declaredVariables) {
                    declaredVariables.add(name)
                } else {
                    reportDuplicatedDeclaration(statement)
                }
                val initializer = statement.initializer
                if (initializer != null) {
                    inspectExpression(declaredVariables, initializedVariables, initializer)
                    initializedVariables.add(name)
                }
            }
            is ReturnStatement -> {
                val result = statement.result
                if (result != null) {
                    inspectExpression(declaredVariables, initializedVariables, result)
                }
            }
            is Expression -> {
                inspectExpression(declaredVariables, initializedVariables, statement)
            }
        }
    }

    private fun inspectExpression(
        declaredVariables: HashSet<String>,
        initializedVariables: HashSet<String>,
        expression: Expression
    ) {
        if (expression is VariableAccess) {
            if (expression.name !in declaredVariables) {
                reportAccessBeforeDeclaration(expression)
            } else {
                if (expression.name !in initializedVariables) {
                    reportAccessBeforeInitialization(expression)
                }
            }
        } else if (expression is FunctionCall) {
            for (argument in expression.arguments) {
                if (argument is VariableAccess) {
                    if (argument.name !in declaredVariables) {
                        reportAccessBeforeDeclaration(argument)
                    } else if (argument.name !in initializedVariables) {
                        reportAccessBeforeInitialization(argument)
                    }
                }
            }
        }
    }

    private fun reportAccessBeforeDeclaration(access: VariableAccess) {
        reporter.report(access, "Variable '${access.name}' is not declared")
    }

    private fun reportAccessBeforeInitialization(access: VariableAccess) {
        reporter.report(access, "Variable '${access.name}' is accessed before initialization")
    }

    private fun reportDuplicatedDeclaration(declaration: VariableDeclaration) {
        reporter.report(declaration, "Variable ${declaration.name} is was already declared")
    }
}