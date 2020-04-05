package org.jetbrains.dummy.lang

import org.junit.Test

class DummyLanguageTestGenerated : AbstractDummyLanguageTest() {
    @Test
    fun testBlocks() {
        doTest("testData/blocks.dummy")
    }
    
    @Test
    fun testCombo() {
        doTest("testData/combo.dummy")
    }
    
    @Test
    fun testExpressions() {
        doTest("testData/expressions.dummy")
    }
    
    @Test
    fun testFunctions() {
        doTest("testData/functions.dummy")
    }
    
    @Test
    fun testGood() {
        doTest("testData/good.dummy")
    }
    
    @Test
    fun testIf() {
        doTest("testData/if.dummy")
    }
    
    @Test
    fun testReturns() {
        doTest("testData/returns.dummy")
    }
    
    @Test
    fun testVariables() {
        doTest("testData/variables.dummy")
    }
}
