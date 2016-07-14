package org.dwallach

import kotlin.test.*
import org.junit.Test

class TreeTest {
    @Test fun emptyTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()

        assertTrue(emptyTree.empty())
    }

    @Test fun insertTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()
        val oneElemTree: Tree<String> = emptyTree.insert("Hello")

        assertFalse(oneElemTree.empty())
    }
}